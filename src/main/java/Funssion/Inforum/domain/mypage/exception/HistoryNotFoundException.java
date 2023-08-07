package Funssion.Inforum.domain.mypage.exception;

import Funssion.Inforum.common.exception.NotFoundException;

public class HistoryNotFoundException extends NotFoundException {
    public HistoryNotFoundException(String message) {
        super(message + "history not found");
    }
    public HistoryNotFoundException() {
        super("history not found");
    }
}
