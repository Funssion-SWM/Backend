package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.response.ErrorResult;
import org.springframework.http.HttpStatus;

public class DeleteFailException extends RuntimeException{
    private ErrorResult errorResult;
    private String message;

    public DeleteFailException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public DeleteFailException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}