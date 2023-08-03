package Funssion.Inforum.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class DuplicateException extends RuntimeException{
    private ErrorResult errorResult;

    public DuplicateException(String message) {
        errorResult = new ErrorResult(HttpStatus.CONFLICT, message);
    }

    public DuplicateException(String message, Throwable cause) {
        super(cause);
        errorResult = new ErrorResult(HttpStatus.CONFLICT, message);
    }
}
