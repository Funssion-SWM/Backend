package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;

import java.util.List;

public interface AnswerRepository {
    Answer createAnswer(Answer answer);

    List<Answer> getAnswersOfQuestion(Long questionId);

    Long getAuthorIdOf(Long answerId);

    Answer updateAnswer(AnswerSaveDto answerSaveDto, Long answerId);
    Answer getAnswerById(Long id);
}
