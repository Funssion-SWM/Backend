package Funssion.Inforum.domain.post.memo.service;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.constant.memo.DateType;
import Funssion.Inforum.common.constant.memo.MemoOrderType;
import Funssion.Inforum.common.exception.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.searchhistory.domain.SearchHistory;
import Funssion.Inforum.domain.post.searchhistory.repository.SearchHistoryRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private final MemoRepository memoRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final MyRepository myRepository;

    @Transactional(readOnly = true)
    public List<MemoListDto> getMemosForMainPage(String period, String orderBy) {

        Long days = getDays(period);

        MemoOrderType memoOrderType = Enum.valueOf(MemoOrderType.class, orderBy.toUpperCase());

        return getMemos(memoOrderType, days);
    }

    private static Long getDays(String period) {

        DateType dateType = Enum.valueOf(DateType.class, period.toUpperCase());
        long days = 0L;

        switch (dateType) {
            case DAY -> days = 1L;
            case WEEK -> days = 7L;
            case MONTH -> days = 31L;
            case YEAR -> days = 365L;
        }

        return days;
    }

    private List<MemoListDto> getMemos(MemoOrderType memoOrderType, Long days) {
        switch (memoOrderType) {
            case NEW -> {
                return memoRepository.findAllOrderById()
                        .stream()
                        .map(MemoListDto::new)
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

    @Transactional(readOnly = true)
    public MemoDto getMemoBy(Long memoId) {

        Memo memo = memoRepository.findById(memoId);
        MemoDto responseDto = new MemoDto(memo);
        responseDto.setIsMine(SecurityContextUtils.getUserId());
        return responseDto;
    }

    @Transactional
    public MemoDto updateMemo(Long memoId, MemoSaveDto form) {

        Long userId = AuthUtils.getUserId(UPDATE);

        Memo savedMemo = memoRepository.findById(memoId);
        updateHistory(form, userId, savedMemo);

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

        memoRepository.delete(memoId);

        if (!memo.getIsTemporary())
            myRepository.updateHistory(userId, MEMO, MINUS, memo.getCreatedDate().toLocalDate());
    }

    public List<MemoListDto> getMemosBy(
            String searchString,
            MemoOrderType orderBy,
            Boolean isRecoded,
            Boolean isTag) {

        if (isTag) throw new BadRequestException("not yet implemented");

        if (isRecoded) saveSearchHistory(searchString, isTag);

        return memoRepository.findAllBySearchQuery(getSearchStringList(searchString), orderBy)
                .stream()
                .map(MemoListDto::new)
                .toList();
    }

    private void saveSearchHistory(String searchString, Boolean isTag) {
        Long userId = SecurityContextUtils.getUserId();

        if (userId.equals(SecurityContextUtils.ANONYMOUS_USER_ID)) return;

        searchHistoryRepository.save(
                SearchHistory.builder()
                        .userId(userId)
                        .searchText(searchString)
                        .isTag(isTag)
                        .build());
    }

    private static List<String> getSearchStringList(String searchString) {
        return Arrays.stream(searchString.split(" "))
                .map(str -> "%" + str + "%")
                .toList();
    }

}
