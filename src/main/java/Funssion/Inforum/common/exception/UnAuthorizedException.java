package Funssion.Inforum.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnAuthorizedException extends RuntimeException{
    private ErrorResult errorResult;

    public UnAuthorizedException(String message) {
        this.errorResult = new ErrorResult(HttpStatus.UNAUTHORIZED, message);
    }

    public UnAuthorizedException(String message, Throwable cause) {
        super(cause);
        this.errorResult = new ErrorResult(HttpStatus.UNAUTHORIZED, message);
    }
}
