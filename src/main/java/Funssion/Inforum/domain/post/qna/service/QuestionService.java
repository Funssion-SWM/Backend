package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.dto.response.QuestionDto;
import Funssion.Inforum.s3.dto.response.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {
    Long getAuthorId(Long questionId);
    Question createQuestion(QuestionSaveDto questionSaveDto, Long authorId, Long memoId);
    Question updateQuestion(QuestionSaveDto questionSaveDto, Long questionId, Long authorId);

    List<Question> getQuestions(OrderType orderBy);

    QuestionDto getOneQuestion(Long questionId);

    IsSuccessResponseDto deleteQuestion(Long questionId, Long authorId);

    List<Question> getQuestionsOfMemo(Long memoId);

    ImageDto saveImageAndGetImageURL(MultipartFile image);
}
