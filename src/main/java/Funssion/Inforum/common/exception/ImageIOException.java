package Funssion.Inforum.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class ImageIOException extends RuntimeException{
    private ErrorResult errorResult;
    private String message;

    public ImageIOException(String message) {
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public ImageIOException(String message, Throwable cause) {
        super(cause);
        this.message = message;
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
