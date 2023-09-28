package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;

import java.util.List;

public interface QuestionRepository {
    Question createQuestion(Question question);

    Question updateQuestion(QuestionSaveDto questionSaveDto, Long questionId);

    List<Question> getQuestions(OrderType orderBy);

    Long getAuthorId(Long questionId);

    Question getOneQuestion(Long questionId);

    void deleteQuestion(Long questionId);

    List<Question> getQuestionsOfMemo(Long memoId);
}
