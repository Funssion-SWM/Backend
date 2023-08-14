package Funssion.Inforum.common.exception.notfound;

import Funssion.Inforum.common.exception.ErrorResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RuntimeException{

    private ErrorResult errorResult;
    private String message;

    public NotFoundException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.NOT_FOUND, message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.NOT_FOUND, message);
    }

}
