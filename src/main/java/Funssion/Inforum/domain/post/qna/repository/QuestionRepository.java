package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.response.UploadedQuestionDto;

public interface QuestionRepository {
    UploadedQuestionDto createQuestion(Question question);
}
