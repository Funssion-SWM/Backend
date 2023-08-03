package Funssion.Inforum.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RuntimeException{

    private ErrorResult errorResult;

    public NotFoundException(String message) {
        errorResult = new ErrorResult(HttpStatus.NOT_FOUND, message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(cause);
        errorResult = new ErrorResult(HttpStatus.NOT_FOUND, message);
    }
}
