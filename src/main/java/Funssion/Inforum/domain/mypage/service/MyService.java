package Funssion.Inforum.domain.mypage.service;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.dto.MyRecordNumDto;
import Funssion.Inforum.domain.mypage.dto.MyUserInfoDto;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyService {

    private final MyRepository myRepository;
    private final MemoRepository memoRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
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

    public List<Question> getMyQuestions(Long userId) {
        return questionRepository.getMyQuestions(userId, OrderType.NEW);
    }

    public List<Question> getMyLikedQuestions(Long userId) {
        return questionRepository.getMyLikedQuestions(userId);
    }

    public List<Question> getQuestionsOfMyAnswer(Long userId){
        return questionRepository.getQuestionsOfMyAnswer(userId);
    }

    public List<Question> getQuestionsOfMyLikedAnswer(Long userId) {
        return questionRepository.getQuestionsOfMyLikedAnswer(userId);
    }
}
