package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.response.ErrorResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnAuthorizedException extends RuntimeException{
    private ErrorResult errorResult;
    private String message;

    public UnAuthorizedException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.UNAUTHORIZED, message);
    }

    public UnAuthorizedException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.UNAUTHORIZED, message);
    }
}
