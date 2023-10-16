package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.GeneralException;
import Funssion.Inforum.common.exception.response.ErrorResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class ImageIOException extends GeneralException {

    public ImageIOException(String message) {
        super(message, new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }

    public ImageIOException(String message, Throwable cause) {
        super(message, new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message), cause);
    }
}
