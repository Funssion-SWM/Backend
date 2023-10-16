package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.GeneralException;
import Funssion.Inforum.common.exception.response.ErrorResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EnumParseException extends GeneralException {

    private static final String message = "error occurs in parse enum value";

    public EnumParseException() {
        super(message, new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }
}
