package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.s3.dto.response.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AnswerService {

    Answer createAnswerOfQuestion(AnswerSaveDto answerSaveDto, Long questionId, Long authorId);

    List<Answer> getAnswersOfQuestion(Long loginId, Long questionId);

    Long getAuthorId(Long answerId);

    Answer updateAnswer(AnswerSaveDto answerSaveDto, Long answerId);

    Answer getAnswerBy(Long answerId);

    void deleteAnswer(Long answerId, Long authorId);

    ImageDto saveImageAndGetImageURL(MultipartFile image);

    Answer selectAnswer(Long loginId, Long questionId, Long answerId);
}
