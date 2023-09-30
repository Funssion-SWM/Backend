package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static Funssion.Inforum.common.constant.PostType.QUESTION;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final MyRepository myRepository;
    @Override
    public Answer createAnswerOfQuestion(AnswerSaveDto answerSaveDto, Long questionId, Long authorId) {
        Answer answer = answerRepository.createAnswer(addAuthorInfo(answerSaveDto, authorId, questionId));
        createOrUpdateHistory(authorId,answer.getCreatedDate(), Sign.PLUS);
        return addAuthorInfo(answerSaveDto,authorId,questionId);
    }
    private Answer addAuthorInfo(AnswerSaveDto answerSaveDto, Long authorId, Long questionId) {
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);
        Answer answerSaveDtoWithAuthor = addAuthor(answerSaveDto, authorId, authorProfile,questionId);
        return answerSaveDtoWithAuthor;
    }
    private Answer addAuthor(AnswerSaveDto answerSaveDto, Long authorId, MemberProfileEntity authorProfile, Long questionId) {
        Long defaultRepliesCount = 0L;
        boolean isSelected = false;
        return new Answer(authorId,authorProfile, LocalDateTime.now(),null, answerSaveDto.getDescription(), answerSaveDto.getText(),questionId,isSelected,defaultRepliesCount);
    }

    private void createOrUpdateHistory(Long userId, LocalDateTime curDate, Sign sign) {
        try {
            myRepository.updateHistory(userId, QUESTION, sign, curDate.toLocalDate());
        } catch (HistoryNotFoundException e) {
            myRepository.createHistory(userId, QUESTION);
        }
    }
}
