package Funssion.Inforum.domain.memo.service;

import Funssion.Inforum.common.constant.memo.DateType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.memo.MemoOrderType;
import Funssion.Inforum.domain.memo.dto.MemoDto;
import Funssion.Inforum.domain.memo.dto.MemoListDto;
import Funssion.Inforum.domain.memo.repository.MemoRepository;
import Funssion.Inforum.domain.memo.dto.MemoSaveDto;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final MyRepository myRepository;

    public List<MemoListDto> getMemosInMainPage(String period, String orderBy) {

        int days = getDays(period);

        MemoOrderType memoOrderType = Enum.valueOf(MemoOrderType.class, orderBy.toUpperCase());

        switch (memoOrderType) {
            case NEW -> {
                return new ArrayList<>(memoRepository.findAllWithNewest());
            }
            //TODO : v2 에서 좋아요 순 정렬 메서드 추가
            case HOT -> throw new IllegalArgumentException("orderBy is undefined value");
            default -> throw new IllegalArgumentException("orderBy is undefined value");
        }
    }

    private static int getDays(String period) {
        DateType dateType = Enum.valueOf(DateType.class, period.toUpperCase());
        int days = 0;
        switch (dateType) {
            case DAY -> days = 1;
            case WEEK -> days = 7;
            case MONTH -> days = 31;
            case YEAR -> days = 365;
            default -> days = 0;
        }
        return days;
    }

    @Transactional
    public MemoDto createMemo(MemoSaveDto form) {
        Integer userId = getUserId();
        String userName = memoRepository.findByUserId(userId);
        Integer memoId = memoRepository.create(userId, userName, form);
        myRepository.updateCreationToHistory(PostType.MEMO, memoId, userId);
        return memoRepository.findById(memoId).orElseThrow();
    }

    private static Integer getUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId == "anonymousUser") {
            return 0;
        }
        return Integer.valueOf(userId);
    }

    public MemoDto getMemoBy(int memoId) {
        MemoDto memo = memoRepository.findById(memoId).orElseThrow(() -> new NoSuchElementException("memo not found"));
        memo.setWriter(memo.getUserId() == getUserId());
        return memo;
    }

    @Transactional
    public MemoDto updateMemo(int memoId, MemoSaveDto form) {
        Integer userId = getUserId();
        if (memoRepository.update(memoId, userId, form) == 0)
            throw new IllegalStateException("update fail");
        return memoRepository.findById(memoId).orElseThrow(() -> new NoSuchElementException("memo not found"));
    }

    @Transactional
    public void deleteMemo(int memoId) {
        Integer userId = getUserId();
        if (memoRepository.delete(memoId) == 0)
            throw new IllegalStateException("delete fail");
        myRepository.updateDeletionToHistory(PostType.MEMO, memoId, userId);
    }
}
