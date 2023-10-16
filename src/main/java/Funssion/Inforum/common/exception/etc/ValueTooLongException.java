package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.GeneralException;
import Funssion.Inforum.common.exception.response.ErrorResult;
import org.springframework.http.HttpStatus;

public class ValueTooLongException extends GeneralException {

    public ValueTooLongException(String message) {
        super(message, new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }

}
