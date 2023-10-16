package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.GeneralException;
import Funssion.Inforum.common.exception.response.ErrorResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ArrayToListException extends GeneralException {

    public ArrayToListException(String message) {
        super(message, new ErrorResult(HttpStatus.BAD_REQUEST, message));
    }

    public ArrayToListException(String message, Throwable cause) {
        super(message, new ErrorResult(HttpStatus.BAD_REQUEST, message), cause);
    }
}