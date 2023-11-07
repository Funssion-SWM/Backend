package Funssion.Inforum.common.exception.forbidden;

import Funssion.Inforum.common.exception.response.ErrorResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends RuntimeException{

    private ErrorResult errorResult;
    private String message;

    public ForbiddenException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.FORBIDDEN, message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.FORBIDDEN, message);
    }
}
