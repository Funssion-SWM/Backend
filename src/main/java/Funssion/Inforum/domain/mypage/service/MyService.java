package Funssion.Inforum.domain.mypage.service;

import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import Funssion.Inforum.domain.member.repository.MemberRepositoryImpl;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.mypage.dto.MyRecordNumDto;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.mypage.dto.MyUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyService {

    private final MyRepository myRepository;
    private final MemoRepository memoRepository;
    private final MemberRepository memberRepository;

    public MyUserInfoDto getUserInfo(Long userId) {
        return MyUserInfoDto.builder()
                .userName(memberRepository.findNameById(userId))
                .build();
    }

    public List<MyRecordNumDto> getHistory(Long userId, Integer year, Integer month) {
        return myRepository.findMonthlyHistoryByUserId(userId, year, month)
                .stream()
                .map(MyRecordNumDto::new)
                .toList();
    }

    public List<MemoListDto> getMyMemos(Long userId) {
        return memoRepository.findAllByUserIdOrderById(userId).stream()
                .map(MemoListDto::new)
                .toList();
    }

    public List<MemoListDto> getMyLikedMemos(Long userId) {
        return memoRepository.findAllLikedMemosByUserId(userId)
                .stream()
                .map(MemoListDto::new)
                .toList();
    }

    public List<MemoListDto> getMyDraftMemos(Long userId) {
        return memoRepository.findAllDraftMemosByUserId(userId)
                .stream()
                .map(MemoListDto::new)
                .toList();
    }
}
