package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;

import java.util.List;

public interface AnswerRepository {
    Answer createAnswer(Answer answer);

    List<Answer> getAnswersOfQuestion(Long loginId, Long questionId);

    Long getAuthorIdOf(Long answerId);

    Answer updateAnswer(AnswerSaveDto answerSaveDto, Long answerId);
    Answer getAnswerById(Long id);
    void updateAnswersCountOfQuestion(Long questionId, Sign sign);
    Answer updateLikesInAnswer(Long answerId,Sign sign);
    Answer updateDisLikesInAnswer(Long disLikes, Long answerId);
    void deleteAnswer(Long answerId);
    Answer select(Long answerId);
    void updateProfileImage(Long userId, String profileImageFilePath);
}
