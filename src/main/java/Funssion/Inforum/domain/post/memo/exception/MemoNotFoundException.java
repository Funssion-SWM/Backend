package Funssion.Inforum.domain.post.memo.exception;

import Funssion.Inforum.common.exception.notfound.NotFoundException;

public class MemoNotFoundException extends NotFoundException {
    public MemoNotFoundException(String message) {
        super(message + " memo not found");
    }

    public MemoNotFoundException() {
        super("memo not found");
    }
}
