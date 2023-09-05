package Funssion.Inforum.domain.post.memo.service;

import Funssion.Inforum.common.constant.memo.DateType;
import Funssion.Inforum.common.constant.memo.MemoOrderType;
import Funssion.Inforum.common.exception.BadRequestException;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoDto;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
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
                memoRepository.create(new Memo(form, authorId, authorProfile, Date.valueOf(LocalDate.now()), null))
        );

        if (!form.getIsTemporary()) createOrUpdateHistory(authorId, createdMemo.getCreatedDate());

        return createdMemo;
    }

    private void createOrUpdateHistory(Long userId, Date curDate) {
        try {
            myRepository.updateHistory(userId, MEMO, PLUS, curDate);
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

        return new MemoDto(memo);
    }

    @Transactional
    public MemoDto updateMemo(Long memoId, MemoSaveDto form) {

        Long userId = AuthUtils.getUserId(UPDATE);

        Memo memo = memoRepository.updateContentInMemo(form, memoId);

        return new MemoDto(memo);
    }

    @Transactional
    public void deleteMemo(Long memoId) {

        Long userId = AuthUtils.getUserId(DELETE);
        Memo memo = memoRepository.findById(memoId);

        memoRepository.delete(memoId);

        myRepository.updateHistory(userId, MEMO, MINUS, memo.getCreatedDate());
    }

    @Transactional(readOnly = true)
    public List<MemoListDto> getMemosBy(String searchString, String orderBy) {

        MemoOrderType memoOrderType = MemoOrderType.valueOf(orderBy.toUpperCase());
        List<String> searchStringList = Arrays.stream(searchString.split(" "))
                .map(str -> "%" + str + "%")
                .toList();

        return memoRepository
                .findAllBySearchQuery(searchStringList, memoOrderType)
                .stream()
                .map(MemoListDto::new)
                .toList();
    }

}
