package Funssion.Inforum.common.exception;

import org.springframework.http.HttpStatus;

public class CreatedRowException extends RuntimeException{
    private ErrorResult errorResult;
    private String message;

    public CreatedRowException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public CreatedRowException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
