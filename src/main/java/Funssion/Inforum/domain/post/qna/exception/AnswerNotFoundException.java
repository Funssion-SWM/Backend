package Funssion.Inforum.domain.post.qna.exception;

import Funssion.Inforum.common.exception.notfound.NotFoundException;

public class AnswerNotFoundException extends NotFoundException {
    public AnswerNotFoundException(String message) {
        super(message+ " answer not found");
    }

    public AnswerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
