package Funssion.Inforum.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends RuntimeException{
    private ErrorResult errorResult;
    private String message;

    public BadRequestException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.BAD_REQUEST, message);
    }
}
