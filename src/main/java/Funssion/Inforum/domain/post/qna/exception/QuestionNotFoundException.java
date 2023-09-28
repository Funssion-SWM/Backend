package Funssion.Inforum.domain.post.qna.exception;

import Funssion.Inforum.common.exception.notfound.NotFoundException;

public class QuestionNotFoundException extends NotFoundException {
    public QuestionNotFoundException(String message) {
        super(message + " question not found");
    }

    public QuestionNotFoundException() {
        super("question not found");
    }
}

