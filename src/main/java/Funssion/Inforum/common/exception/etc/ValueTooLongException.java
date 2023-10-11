package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.response.ErrorResult;
import org.springframework.http.HttpStatus;

public class ValueTooLongException extends RuntimeException{
    private ErrorResult errorResult;
    private String message;

    public ValueTooLongException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
