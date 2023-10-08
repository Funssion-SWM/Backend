package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.response.ErrorResult;
import org.springframework.http.HttpStatus;

public class UpdateFailException extends RuntimeException{
    private ErrorResult errorResult;
    private String message;

    public UpdateFailException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public UpdateFailException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
