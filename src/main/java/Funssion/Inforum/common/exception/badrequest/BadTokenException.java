package Funssion.Inforum.common.exception.badrequest;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;

public class BadTokenException extends BadRequestException {
    public BadTokenException(String message) {
        super(message);
    }

    public BadTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
