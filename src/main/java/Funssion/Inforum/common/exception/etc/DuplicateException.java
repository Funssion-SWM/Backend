package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.GeneralException;
import Funssion.Inforum.common.exception.response.ErrorResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class DuplicateException extends GeneralException {
    public
    DuplicateException(String message) {
        super(message, new ErrorResult(HttpStatus.CONFLICT, message));
    }

    public DuplicateException(String message, Throwable cause) {
        super(message, new ErrorResult(HttpStatus.CONFLICT, message), cause);
    }
}
