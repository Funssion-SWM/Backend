package Funssion.Inforum.common.exception.etc;

import Funssion.Inforum.common.exception.response.ErrorResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EnumParseException extends RuntimeException {
    private final ErrorResult errorResult;
    private final String message = "error occurs in parse enum value";

    public EnumParseException() {
        this.errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
