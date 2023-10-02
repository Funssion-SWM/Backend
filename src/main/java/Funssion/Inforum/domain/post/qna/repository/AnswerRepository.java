package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.domain.post.qna.domain.Answer;

import java.util.List;

public interface AnswerRepository {
    Answer createAnswer(Answer answer);

    List<Answer> getAnswersOfQuestion(Long questionId);
}
