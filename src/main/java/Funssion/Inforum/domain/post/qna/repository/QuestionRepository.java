package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;

import java.util.List;

public interface QuestionRepository {
    Question createQuestion(Question question);

    Question updateQuestion(QuestionSaveDto questionSaveDto, Long questionId);

    List<Question> getQuestions(Long userId, OrderType orderBy);

    Long getAuthorId(Long questionId);

    Question getOneQuestion(Long questionId);

    void deleteQuestion(Long questionId);
    void solveQuestion(Long questionId);
    Question updateLikesInQuestion(Long likes, Long questionId);

    List<Question> getQuestionsOfMemo(Long userId, Long memoId);

    List<Question> findAllBySearchQuery(List<String> searchStringList, OrderType orderType);
    List<Question> findAllByTag(String tagText, OrderType orderType);
    List<Question> findAllByTag(String tagText, Long userId, OrderType orderType);

    void updateProfileImage(Long userId, String profileImageFilePath);
}
