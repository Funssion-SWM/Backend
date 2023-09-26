package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.domain.post.qna.dto.response.UploadedQuestionDto;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;

public interface QuestionService {
    UploadedQuestionDto createQuestion(QuestionSaveDto questionSaveDto, Long authorId);
}
