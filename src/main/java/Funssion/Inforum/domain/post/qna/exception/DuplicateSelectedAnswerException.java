package Funssion.Inforum.domain.post.qna.exception;

import Funssion.Inforum.common.exception.etc.DuplicateException;

public class DuplicateSelectedAnswerException extends DuplicateException {
    public DuplicateSelectedAnswerException(String message) {
        super(message);
    }
}
