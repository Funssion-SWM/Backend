package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.GeneralException;
import Funssion.Inforum.common.exception.response.ErrorResult;
import org.springframework.http.HttpStatus;

public class EnumTypeMisMatchException extends GeneralException {
    private static String message = "unavailable enum type";
    public EnumTypeMisMatchException() {
        super(message, new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }
}
