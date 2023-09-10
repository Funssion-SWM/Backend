package Funssion.Inforum.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class DuplicateException extends RuntimeException{
    private ErrorResult errorResult;
    private String message;

    public
    DuplicateException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.CONFLICT, message);
    }

    public DuplicateException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.CONFLICT, message);
    }
}
