package Funssion.Inforum.memo.service;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.memo.dto.MemoDto;
import Funssion.Inforum.memo.dto.MemoListDto;
import Funssion.Inforum.memo.dto.MemoSaveDto;
import Funssion.Inforum.memo.repository.MemoRepository;
import Funssion.Inforum.mypage.repository.MyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemoService {

    private static Map<String, Integer> periodToDaysMap;
    private final MemoRepository memoRepository;
    private final MyRepository myRepository;

    @PostConstruct
    private void init() {
        periodToDaysMap = new ConcurrentHashMap<>();
        periodToDaysMap.put("day", 1);
        periodToDaysMap.put("week", 7);
        periodToDaysMap.put("month", 30);
        periodToDaysMap.put("year", 365);
    }

    public ArrayList<MemoListDto> getMemosInMainPage(String period, String orderBy) {

        int days = getDays(period);

        log.debug("orderby value = {}",orderBy);
        if (orderBy.equals("new")) {
            return new ArrayList<>(memoRepository.findAllWithNewest());
        } else if (orderBy.isEmpty() || orderBy.equals("hot")) {
            // TODO: 좋아요 필드 추가되면 repository 넣기
            throw new InvalidParameterException("orderBy is undefined value");
        } else {
            throw new InvalidParameterException("orderBy is undefined value");
        }
    }

    private static int getDays(String period) {
        if (period.isEmpty()) period = "day";

        Integer days = periodToDaysMap.get(period);
        if (days == null) {
            throw new InvalidParameterException("period is undefined value");
        }
        return days;
    }

    @Transactional
    public MemoDto createMemo(MemoSaveDto form) {
        // TODO: jwt 토큰에서 userId, userName 가져오기
        MemoDto memoDto = memoRepository.create(1, "정진우", form);
        myRepository.updateHistory(PostType.MEMO, memoDto.getMemoId(), 1);
        return memoDto;
    }

    public MemoDto getMemoBy(int memoId) {
        return memoRepository.findById(memoId);
    }

    public MemoDto updateMemo(int memoId, MemoSaveDto form) {
        // TODO: jwt 토큰에서 userId 가져오기
        return memoRepository.update(memoId, form);
    }

    public void deleteMemo(int memoId) {
        memoRepository.delete(memoId);
    }
}
