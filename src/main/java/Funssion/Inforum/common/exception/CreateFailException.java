package Funssion.Inforum.common.exception;

import org.springframework.http.HttpStatus;

public class CreateFailException extends RuntimeException{
    private ErrorResult errorResult;
    private String message;

    public CreateFailException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public CreateFailException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
