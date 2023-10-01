package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;

public interface AnswerService {

    Answer createAnswerOfQuestion(AnswerSaveDto answerSaveDto, Long questionId, Long authorId);
}
