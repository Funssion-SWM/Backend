package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.GeneralException;
import Funssion.Inforum.common.exception.response.ErrorResult;
import org.springframework.http.HttpStatus;

public class StringParseException extends GeneralException {
    private static final String message = "error occurs in parse string value";

    public StringParseException() {
        super(message, new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }
}
