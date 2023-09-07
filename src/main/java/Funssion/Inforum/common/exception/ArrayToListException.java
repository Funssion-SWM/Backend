package Funssion.Inforum.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ArrayToListException extends RuntimeException{
    private ErrorResult errorResult;
    private String message;

    public ArrayToListException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.BAD_REQUEST, message);
    }

    public ArrayToListException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.BAD_REQUEST, message);
    }
}