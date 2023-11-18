package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.GeneralException;
import Funssion.Inforum.common.exception.response.ErrorResult;
import org.springframework.http.HttpStatus;

public class DeleteFailException extends GeneralException {
    private static final String DEFAULT_MESSAGE = "delete failed: ";

    public DeleteFailException(String message) {
        super(DEFAULT_MESSAGE + message, new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }


    public DeleteFailException(String message, Throwable cause) {
        super(DEFAULT_MESSAGE + message, new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message), cause);
    }
}