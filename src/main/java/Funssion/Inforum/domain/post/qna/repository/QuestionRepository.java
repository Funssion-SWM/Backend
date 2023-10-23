package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;

import java.util.List;

public interface QuestionRepository {
    Question createQuestion(Question question);

    Question updateQuestion(QuestionSaveDto questionSaveDto, Long questionId);

    List<Question> getQuestions(Long userId, OrderType orderBy, Long pageNum, Long resultCntPerPage);
    List<Question> getMyQuestions(Long userId, OrderType orderBy, Long pageNum, Long resultCntPerPage);


    Long getAuthorId(Long questionId);

    Question getOneQuestion(Long questionId);

    void deleteQuestion(Long questionId);
    void solveQuestion(Long questionId);
    Question updateLikesInQuestion(Long questionId, Sign sign);

    List<Question> getQuestionsOfMemo(Long userId, Long memoId);

    List<Question> findAllBySearchQuery(List<String> searchStringList, OrderType orderType, Long pageNum, Long resultCntPerPage);
    List<Question> findAllByTag(String tagText, OrderType orderType, Long pageNum, Long resultCntPerPage);
    List<Question> findAllByTag(String tagText, Long userId, OrderType orderType, Long pageNum, Long resultCntPerPage);

    void updateProfileImage(Long userId, String profileImageFilePath);
    List<Question> getMyLikedQuestions(Long userId, Long pageNum, Long resultCntPerPage);

    List<Question> getQuestionsOfMyAnswer(Long userId, Long pageNum, Long resultCntPerPage);

    List<Question> getQuestionsOfMyLikedAnswer(Long userId, Long pageNum, Long resultCntPerPage);
}
