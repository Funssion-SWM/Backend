package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.GeneralException;
import Funssion.Inforum.common.exception.response.ErrorResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnAuthorizedException extends GeneralException {

    public UnAuthorizedException(String message) {
        super(message, new ErrorResult(HttpStatus.UNAUTHORIZED, message));
    }

    public UnAuthorizedException(String message, Throwable cause) {
        super(message, new ErrorResult(HttpStatus.UNAUTHORIZED, message), cause);
    }
}
