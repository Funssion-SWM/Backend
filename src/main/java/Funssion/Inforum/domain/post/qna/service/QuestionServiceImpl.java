package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static Funssion.Inforum.common.constant.PostType.QUESTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final MyRepository myRepository;

    @Override
    public Long getAuthorId(Long questionId) {
        return questionRepository.getAuthorId(questionId);
    }

    @Override
    @Transactional
    public Question createQuestion(QuestionSaveDto questionSaveDto, Long authorId)
    {
        Question question = questionRepository.createQuestion(addAuthorInfo(questionSaveDto, authorId));
        createOrUpdateHistory(authorId,question.getCreatedDate(),Sign.PLUS);
        return question;
    }

    private Question addAuthorInfo(QuestionSaveDto questionSaveDto, Long authorId) {
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);
        Question questionSaveDtoWithAuthor = addAuthor(questionSaveDto, authorId, authorProfile);
        return questionSaveDtoWithAuthor;
    }

    private void createOrUpdateHistory(Long userId, LocalDateTime curDate, Sign sign) {
        try {
            myRepository.updateHistory(userId, QUESTION, sign, curDate.toLocalDate());
        } catch (HistoryNotFoundException e) {
            myRepository.createHistory(userId, QUESTION);
        }
    }
    @Override
    @Transactional
    public Question updateQuestion(QuestionSaveDto questionSaveDto, Long questionId, Long authorId) {
        Question savedQuestion = questionRepository.getOneQuestion(questionId);
        Question question = questionRepository.updateQuestion(questionSaveDto, questionId);
        return question;
    }

    @Override
    public List<Question> getQuestions(OrderType orderBy) {
        return questionRepository.getQuestions(orderBy);
    }

    @Override
    public Question getOneQuestion(Long questionId) {
        return questionRepository.getOneQuestion(questionId);
    }

    @Override
    @Transactional
    public IsSuccessResponseDto deleteQuestion(Long questionId, Long authorId) {
        Question deletedQuestion = questionRepository.getOneQuestion(questionId);
        questionRepository.deleteQuestion(questionId);
        myRepository.updateHistory(authorId,QUESTION,Sign.MINUS,deletedQuestion.getCreatedDate().toLocalDate());
        return new IsSuccessResponseDto(true,"성공적으로 질문이 삭제되었습니다.");
    }

    private Question addAuthor(Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto questionSaveDto, Long authorId, MemberProfileEntity authorProfile) {
        Long defaultLikesCount = 0L;
        Long defaultAnswersCount = 0L;
        boolean isSolved = false;
        return new Question(authorId,authorProfile,LocalDateTime.now(),null, questionSaveDto.getTitle(), questionSaveDto.getText(), questionSaveDto.getTags(),defaultAnswersCount,defaultLikesCount,isSolved);
    }
}
