package Funssion.Inforum.memo.service;

import Funssion.Inforum.memo.entity.Memo;
import Funssion.Inforum.memo.form.MemoSaveForm;
import Funssion.Inforum.memo.repository.MemoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MemoService {

    private static Map<String, Integer> periodToDaysMap;
    private final MemoRepository memoRepository;

    @PostConstruct
    private void init() {
        periodToDaysMap = new ConcurrentHashMap<>();
        periodToDaysMap.put("day", 1);
        periodToDaysMap.put("week", 7);
        periodToDaysMap.put("month", 30);
        periodToDaysMap.put("year", 365);
    }

    public ArrayList<Memo> getMemosBy(String period, String orderBy, String userId) {

        if (!userId.isEmpty()) {
            return new ArrayList<>(memoRepository.findAllByUserId(Integer.valueOf(userId)));
        }

        int days = getDays(period);

        if (orderBy == "new") {
            return new ArrayList<>(memoRepository.findAllByPeriod(days, "created_date"));
        } else if (orderBy.isEmpty() || orderBy == "hot") {
            // TODO: 좋아요 필드 추가되면 orderByField로 넣기
            throw  new InvalidParameterException("orderBy is undefined value");
        } else {
            throw  new InvalidParameterException("orderBy is undefined value");
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

    public Memo createMemo(MemoSaveForm form) {
        // TODO: jwt 토큰에서 userId, userName 가져오기
        return memoRepository.create(1, "정진우", form);
    }

    public Memo getMemoBy(String memoId) {
        return memoRepository.findById(Integer.valueOf(memoId));
    }

    public Memo updateMemo(String memoId, MemoSaveForm form) {
        // TODO: jwt 토큰에서 userId 가져오기
        return memoRepository.update(Integer.valueOf(memoId), form);
    }

    public void deleteMemo(String memoId) {
        memoRepository.delete(Integer.valueOf(memoId));
    }
}
