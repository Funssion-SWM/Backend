package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.GeneralException;
import Funssion.Inforum.common.exception.response.ErrorResult;
import org.springframework.http.HttpStatus;

public class DeleteFailException extends GeneralException {

    public DeleteFailException(String message) {
        super(message, new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }

    public DeleteFailException(String message, Throwable cause) {
        super(message, new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message), cause);
    }
}