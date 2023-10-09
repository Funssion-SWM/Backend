package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;

import java.util.List;

public interface QuestionRepository {
    Question createQuestion(Question question);

    Question updateQuestion(QuestionSaveDto questionSaveDto, Long questionId);

    List<Question> getQuestions(Long userId, OrderType orderBy);
    List<Question> getMyQuestions(Long userId, OrderType orderBy);


    Long getAuthorId(Long questionId);

    Question getOneQuestion(Long questionId);

    void deleteQuestion(Long questionId);
    void solveQuestion(Long questionId);
    Question updateLikesInQuestion(Long likes, Long questionId);

    List<Question> getQuestionsOfMemo(Long userId, Long memoId);

    void updateProfileImage(Long userId, String profileImageFilePath);
    List<Question> getMyLikedQuestions(Long userId);

    List<Question> getQuestionsOfMyAnswer(Long userId);

    List<Question> getQuestionsOfMyLikedAnswer(Long userId);
}
