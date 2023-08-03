package Funssion.Inforum.domain.memo.exception;

import Funssion.Inforum.common.exception.NotFoundException;

public class MemoNotFoundException extends NotFoundException {
    public MemoNotFoundException(String message) {
        super(message + " memo not found");
    }

    public MemoNotFoundException() {
        super("memo not found");
    }
}
