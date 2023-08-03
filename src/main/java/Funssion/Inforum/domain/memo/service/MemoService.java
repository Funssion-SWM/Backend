package Funssion.Inforum.domain.memo.service;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.constant.memo.DateType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.memo.MemoOrderType;
import Funssion.Inforum.common.exception.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.member.repository.NonSocialMemberRepository;
import Funssion.Inforum.domain.memo.dto.response.MemoDto;
import Funssion.Inforum.domain.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.memo.entity.Memo;
import Funssion.Inforum.domain.memo.exception.NeedAuthenticationException;
import Funssion.Inforum.domain.memo.repository.MemoRepository;
import Funssion.Inforum.domain.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final MyRepository myRepository;
    private final NonSocialMemberRepository memberRepository;

    public List<MemoListDto> getMemosForMainPage(String period, String orderBy) {

        int days = getDays(period);

        MemoOrderType memoOrderType = Enum.valueOf(MemoOrderType.class, orderBy.toUpperCase());

        return getMemos(memoOrderType);
    }

    private static int getDays(String period) {

        DateType dateType = Enum.valueOf(DateType.class, period.toUpperCase());
        int days = 0;

        switch (dateType) {
            case DAY -> days = 1;
            case WEEK -> days = 7;
            case MONTH -> days = 31;
            case YEAR -> days = 365;
        }

        return days;
    }

    private List<MemoListDto> getMemos(MemoOrderType memoOrderType) {
        switch (memoOrderType) {
            case NEW -> {
                return memoRepository.findAllOrderById().stream().map(memo -> new MemoListDto(memo)).toList();
            }
            //TODO : v2 에서 좋아요 순 정렬 메서드 추가
            case HOT -> throw new BadRequestException("orderBy is undefined value");
            default -> throw new BadRequestException("orderBy is undefined value");
        }
    }

    @Transactional
    public MemoDto createMemo(MemoSaveDto form) {

        Integer userId = getUserId(CRUDType.CREATE);
        String userName = memberRepository.findNameById(userId);

        MemoDto createdMemo = new MemoDto(
                memoRepository.create(new Memo(form, userId, userName, Date.valueOf(LocalDate.now()), null))
        );

        myRepository.updateCreationToHistory(PostType.MEMO, createdMemo.getMemoId(), userId);
        return createdMemo;
    }

    public MemoDto getMemoBy(int memoId) {
        return new MemoDto(memoRepository.findById(memoId));
    }

    @Transactional
    public MemoDto updateMemo(int memoId, MemoSaveDto form) {

        Integer userId = getUserId(CRUDType.UPDATE);

        MemoDto updatedMemo = new MemoDto(
                memoRepository.update(new Memo(form, memoId, Date.valueOf(LocalDate.now())), memoId)
        );

        return updatedMemo;
    }

    @Transactional
    public void deleteMemo(int memoId) {

        Integer userId = getUserId(CRUDType.DELETE);

        memoRepository.delete(memoId);
        myRepository.updateDeletionToHistory(PostType.MEMO, memoId, userId);
    }

    private static Integer getUserId(CRUDType type) {
        Integer userId = SecurityContextUtils.getUserId();

        if (userId != 0) return userId;

        throw new NeedAuthenticationException(type.toString().toLowerCase() + " fail");
    }
}
