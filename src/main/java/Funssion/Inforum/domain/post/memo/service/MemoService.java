package Funssion.Inforum.domain.post.memo.service;

import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.exception.etc.ArrayToListException;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.common.exception.forbidden.ForbiddenException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.follow.repository.FollowRepository;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import Funssion.Inforum.domain.score.repository.ScoreRepository;
import Funssion.Inforum.domain.score.service.ScoreService;
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
import java.util.List;
import java.util.Objects;

import static Funssion.Inforum.common.constant.CRUDType.*;
import static Funssion.Inforum.common.constant.NotificationType.NEW_POST_FOLLOWED;
import static Funssion.Inforum.common.constant.PostType.MEMO;
import static Funssion.Inforum.common.constant.Sign.MINUS;
import static Funssion.Inforum.common.constant.Sign.PLUS;
import static Funssion.Inforum.common.utils.CustomStringUtils.getSearchStringList;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemoService {

    @Value("${aws.s3.memo-dir}")
    private String MEMO_DIR;

    private final ScoreService scoreService;

    private final MemoRepository memoRepository;
    private final TagRepository tagRepository;
    private final MyRepository myRepository;
    private final ScoreRepository scoreRepository;
    private final S3Repository s3Repository;
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;

    public List<MemoListDto> getMemosForMainPage(DateType period, OrderType orderBy, Long pageNum, Long resultCntPerPage) {

        return getMemos(orderBy, period, pageNum, resultCntPerPage);
    }

    private List<MemoListDto> getMemos(OrderType memoOrderType, DateType period, Long pageNum, Long resultCntPerPage) {
        switch (memoOrderType) {
            case NEW -> {
                return memoRepository.findAllOrderById(pageNum, resultCntPerPage)
                        .stream()
                        .map((MemoListDto::new))
                        .toList();
            }
            case HOT -> {
                return memoRepository.findAllByDaysOrderByLikes(period, pageNum, resultCntPerPage)
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

        Memo createdMemo = memoRepository.create(new Memo(form, authorId, authorProfile, LocalDateTime.now(), LocalDateTime.now()));

        tagRepository.saveTags(createdMemo.getId(),form.getMemoTags());

        if (!form.getIsTemporary()) {
            createOrUpdateHistory(authorId, createdMemo.getCreatedDate(), PLUS);
            scoreService.checkUserDailyScoreAndAdd(authorId,ScoreType.MAKE_MEMO, createdMemo.getId());
            sendNotificationToFollower(authorId, createdMemo);
        }

        return new MemoDto(createdMemo);
    }

    private void createOrUpdateHistory(Long userId, LocalDateTime curDate, Sign sign) {
        try {
            myRepository.updateHistory(userId, MEMO, sign, curDate.toLocalDate());
        } catch (HistoryNotFoundException e) {
            myRepository.createHistory(userId, MEMO);
        }
    }

    private void sendNotificationToFollower(Long senderId, Memo createdMemo) {
        List<Long> followerIdList =
                followRepository.findFollowedUserIdByUserId(senderId);

        for (Long receiverId : followerIdList) {
            notificationRepository.save(
                    Notification.builder()
                            .receiverId(receiverId)
                            .postTypeToShow(MEMO)
                            .postIdToShow(createdMemo.getId())
                            .senderId(createdMemo.getAuthorId())
                            .senderPostType(MEMO)
                            .senderPostId(createdMemo.getId())
                            .senderName(createdMemo.getAuthorName())
                            .senderImagePath(createdMemo.getAuthorImagePath())
                            .senderRank(createdMemo.getRank())
                            .notificationType(NEW_POST_FOLLOWED)
                            .build()
            );
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
        Long userId = SecurityContextUtils.getUserId();
        if (memo.getIsTemporary() && !userId.equals(memo.getAuthorId())) {
            throw new ForbiddenException("남의 임시메모에는 접근할 수 없습니다.");
        }

        MemoDto responseDto = new MemoDto(memo);
        responseDto.setIsMine(SecurityContextUtils.getUserId());
        return responseDto;
    }

    @Transactional
    public MemoDto updateMemo(Long memoId, MemoSaveDto form) {

        Long userId = AuthUtils.getUserId(UPDATE);
        ArrayList<String> updatedTags = new ArrayList<>(form.getMemoTags());
        Memo willBeUpdatedMemo = memoRepository.findById(memoId);

        checkPermission(userId, willBeUpdatedMemo);
        checkUpdatableMemo(willBeUpdatedMemo, form);

        updateHistory(form, userId, willBeUpdatedMemo);
        try {
            tagRepository.updateTags(memoId,updatedTags);
        } catch (SQLException e) {
            throw new ArrayToListException("tag update 중 sql.Array를 List로 변환하는 중 오류 발생",e);
        }

        return new MemoDto(updateMemo(memoId, form, willBeUpdatedMemo));
    }

    private void checkUpdatableMemo(Memo willBeUpdatedMemo, MemoSaveDto form) {
        // 이미 생성되었고 임시 저장 상태가 아닌 글을 임시저장하려 하면 에러
        if (willBeUpdatedMemo.getIsCreated() && !willBeUpdatedMemo.getIsTemporary() && form.getIsTemporary())
            throw new BadRequestException("이미 생성된 글은 임시저장 할 수 없습니다.");
    }

    private void updateHistory(MemoSaveDto form, Long userId, Memo willBeUpdatedMemo) {
        if(form.getIsTemporary() == willBeUpdatedMemo.getIsTemporary()) return;

        // 임시글 -> 등록
        if (willBeUpdatedMemo.getIsTemporary()) {
            createOrUpdateHistory(userId, willBeUpdatedMemo.getCreatedDate(), PLUS);
            sendNotificationToFollower(userId, willBeUpdatedMemo);
            scoreService.checkUserDailyScoreAndAdd(userId,ScoreType.MAKE_MEMO, willBeUpdatedMemo.getId());
        }
        // 등록된 글 -> 임시글
        else {
            createOrUpdateHistory(userId, willBeUpdatedMemo.getCreatedDate(), MINUS);
            notificationRepository.delete(MEMO, willBeUpdatedMemo.getId());
        }

    }

    private Memo updateMemo(Long memoId, MemoSaveDto form, Memo willBeUpdatedMemo) {
        Memo memo;
        // 실제 메모로 등록된 적이 없는 메모를 등록하려 할 때
        if (!form.getIsTemporary() && willBeUpdatedMemo.getIsTemporary() && !willBeUpdatedMemo.getIsCreated())
            memo = memoRepository.updateContentInMemo(form, memoId, Boolean.TRUE);
        else
            memo = memoRepository.updateContentInMemo(form, memoId);
        return memo;
    }

    @Transactional
    public void deleteMemo(Long memoId) {
        Long userId = AuthUtils.getUserId(DELETE);
        Memo willBeDeletedMemo = memoRepository.findById(memoId);

        checkPermission(userId, willBeDeletedMemo);
        checkDeletableInSeries(willBeDeletedMemo.getSeriesId());

        try {
            tagRepository.deleteTags(memoId);
        } catch (SQLException e) {
            throw new ArrayToListException("tag update 중 sql.Array를 List로 변환하는 중 오류 발생",e);
        }
        memoRepository.delete(memoId);

        s3Repository.deleteAll("memos/" + memoId);

        if (!willBeDeletedMemo.getIsTemporary()) {
            myRepository.updateHistory(userId, MEMO, MINUS, willBeDeletedMemo.getCreatedDate().toLocalDate());
            scoreService.subtractUserScore(userId,ScoreType.MAKE_MEMO,willBeDeletedMemo.getId());
            notificationRepository.delete(MEMO, willBeDeletedMemo.getId());
        }
    }

    private void checkDeletableInSeries(Long seriesId) {
        if (Objects.nonNull(seriesId) && memoRepository.findAllBySeriesId(seriesId).size() <= 2)
            throw new BadRequestException("속한 시리즈의 남은 메모가 2개 이하가 되어 삭제할 수 없습니다. 시리즈를 먼저 삭제해주세요.");
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
            Boolean isTag,
            Long pageNum,
            Long resultCntPerPage) {

        if (isTag)
            return getMemoListDtosSearchedByTag(searchString, userId, orderBy, pageNum, resultCntPerPage);

        return memoRepository.findAllBySearchQuery(getSearchStringList(searchString), orderBy, userId, pageNum, resultCntPerPage)
                .stream()
                .map(MemoListDto::new)
                .toList();
    }

    private List<MemoListDto> getMemoListDtosSearchedByTag(String searchString, Long userId, OrderType orderBy, Long pageNum, Long resultCntPerPage) {
        List<Memo> result;
        if (userId.equals(SecurityContextUtils.ANONYMOUS_USER_ID))
            result = memoRepository.findAllByTag(searchString, orderBy, pageNum, resultCntPerPage);
        else result = memoRepository.findAllByTag(searchString, userId, orderBy, pageNum, resultCntPerPage);

        return result.stream()
                .map(MemoListDto::new)
                .toList();
    }

}
