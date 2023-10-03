package Funssion.Inforum.domain.post.memo.service;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.exception.etc.ArrayToListException;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import Funssion.Inforum.domain.tag.repository.TagRepository;
import Funssion.Inforum.s3.S3Repository;
import Funssion.Inforum.s3.S3Utils;
import Funssion.Inforum.s3.dto.response.ImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Funssion.Inforum.common.constant.CRUDType.*;
import static Funssion.Inforum.common.constant.PostType.MEMO;
import static Funssion.Inforum.common.constant.Sign.MINUS;
import static Funssion.Inforum.common.constant.Sign.PLUS;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemoService {

    @Value("${aws.s3.memo-dir}")
    private String MEMO_DIR;

    private final MemoRepository memoRepository;
    private final TagRepository tagRepository;
    private final MyRepository myRepository;
    private final S3Repository s3Repository;

    public List<MemoListDto> getMemosForMainPage(DateType date, OrderType orderBy) {

        Integer days = DateType.toNumOfDays(date);

        return getMemos(orderBy, days);
    }

    private List<MemoListDto> getMemos(OrderType memoOrderType, Integer days) {
        switch (memoOrderType) {
            case NEW -> {
                return memoRepository.findAllOrderById()
                        .stream()
                        .map((MemoListDto::new))
                        .toList();
            }
            case HOT -> {
                return memoRepository.findAllByDaysOrderByLikes(days)
                        .stream()
                        .map(MemoListDto::new)
                        .toList();
            }
            default -> throw new BadRequestException("orderBy is undefined value");
        }
    }

    @Transactional
    public MemoDto createMemo(MemoSaveDto form) {

        Long authorId = AuthUtils.getUserId(CREATE);

        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);

        MemoDto createdMemo = new MemoDto(
                memoRepository.create(new Memo(form, authorId, authorProfile, LocalDateTime.now(), LocalDateTime.now()))
        );
        tagRepository.saveTags(createdMemo.getMemoId(),form.getMemoTags());

        if (!form.getIsTemporary())
            createOrUpdateHistory(authorId, createdMemo.getCreatedDate(), PLUS);

        return createdMemo;
    }

    private void createOrUpdateHistory(Long userId, LocalDateTime curDate, Sign sign) {
        try {
            myRepository.updateHistory(userId, MEMO, sign, curDate.toLocalDate());
        } catch (HistoryNotFoundException e) {
            myRepository.createHistory(userId, MEMO);
        }
    }

    public List<MemoListDto> getDraftMemos() {

        Long authorId = AuthUtils.getUserId(READ);

        return memoRepository.findAllDraftMemosByUserId(authorId).stream()
                .map(MemoListDto::new)
                .toList();
    }

    public MemoDto getMemoBy(Long memoId) {

        Memo memo = memoRepository.findById(memoId);
        MemoDto responseDto = new MemoDto(memo);
        responseDto.setIsMine(SecurityContextUtils.getUserId());
        return responseDto;
    }

    @Transactional
    public MemoDto updateMemo(Long memoId, MemoSaveDto form) {

        Long userId = AuthUtils.getUserId(UPDATE);
        ArrayList<String> updatedTags = new ArrayList<>(form.getMemoTags());
        Memo savedMemo = memoRepository.findById(memoId);
        checkPermission(userId, savedMemo);

        updateHistory(form, userId, savedMemo);
        try {
            tagRepository.updateTags(memoId,updatedTags);
        } catch (SQLException e) {
            throw new ArrayToListException("tag update 중 sql.Array를 List로 변환하는 중 오류 발생",e);
        }
        Memo memo = memoRepository.updateContentInMemo(form, memoId);

        return new MemoDto(memo);
    }

    private void updateHistory(MemoSaveDto form, Long userId, Memo savedMemo) {
        if(form.getIsTemporary() == savedMemo.getIsTemporary()) return;

        // 임시글 -> 등록
        if (savedMemo.getIsTemporary())
            createOrUpdateHistory(userId, savedMemo.getCreatedDate(), PLUS);
        // 등록된 글 -> 임시글
        else
            createOrUpdateHistory(userId, savedMemo.getCreatedDate(), MINUS);

    }

    @Transactional
    public void deleteMemo(Long memoId) {
        Long userId = AuthUtils.getUserId(DELETE);
        Memo memo = memoRepository.findById(memoId);

        checkPermission(userId, memo);

        try {
            tagRepository.deleteTags(memoId);
        } catch (SQLException e) {
            throw new ArrayToListException("tag update 중 sql.Array를 List로 변환하는 중 오류 발생",e);
        }
        memoRepository.delete(memoId);

        s3Repository.deleteAll("memos/" + memoId);

        if (!memo.getIsTemporary())
            myRepository.updateHistory(userId, MEMO, MINUS, memo.getCreatedDate().toLocalDate());
    }

    private static void checkPermission(Long userId, Memo savedMemo) {
        if (!userId.equals(savedMemo.getAuthorId()))
            throw new UnAuthorizedException("메모를 업데이트할 권한이 없습니다.");
    }


    public ImageDto uploadImageInMemo(Long id, MultipartFile image) {
        Long userId = AuthUtils.getUserId(UPDATE);

        String imageName = S3Utils.generateImageNameOfS3(userId);

        String bucketName = s3Repository.createFolder(MEMO_DIR, id.toString());
        String uploadedURL = s3Repository.upload(image, bucketName, imageName);

        return ImageDto.builder()
                .imageName(imageName)
                .imagePath(uploadedURL)
                .build();
    }

    public List<MemoListDto> searchMemosBy(
            String searchString,
            Long userId,
            OrderType orderBy,
            Boolean isTag) {

        if (isTag)
            return getMemoListDtosSearchedByTag(searchString, userId, orderBy);

        return memoRepository.findAllBySearchQuery(getSearchStringList(searchString), orderBy)
                .stream()
                .map(MemoListDto::new)
                .toList();
    }

    private List<MemoListDto> getMemoListDtosSearchedByTag(String searchString, Long userId, OrderType orderBy) {
        List<Memo> result;
        if (userId.equals(SecurityContextUtils.ANONYMOUS_USER_ID))
            result = memoRepository.findAllByTag(searchString, orderBy);
        else result = memoRepository.findAllByTag(searchString, userId, orderBy);

        return result.stream()
                .map(MemoListDto::new)
                .toList();
    }

    private static List<String> getSearchStringList(String searchString) {
        return Arrays.stream(searchString.split(" "))
                .map(str -> "%" + str + "%")
                .toList();
    }

}
