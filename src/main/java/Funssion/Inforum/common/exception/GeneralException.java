package Funssion.Inforum.common.exception;

import Funssion.Inforum.common.exception.response.ErrorResult;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException{
    private ErrorResult errorResult;
    private String message;

    public GeneralException(String message, ErrorResult errorResult) {
        super(message);
        this.message = message;
        this.errorResult = errorResult;
    }

    public GeneralException(String message, ErrorResult errorResult, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.errorResult = errorResult;
    }
}
