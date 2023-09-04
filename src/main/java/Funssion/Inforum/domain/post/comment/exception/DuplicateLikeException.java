package Funssion.Inforum.domain.post.comment.exception;

import Funssion.Inforum.common.exception.DuplicateException;

public class DuplicateLikeException extends DuplicateException {
    public DuplicateLikeException(String message) {
        super(message);
    }

    public DuplicateLikeException(String message, Throwable cause) {
        super(message, cause);
    }
}
