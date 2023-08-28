package Funssion.Inforum.domain.like.exception;

import Funssion.Inforum.common.exception.ErrorResult;
import Funssion.Inforum.common.exception.notfound.NotFoundException;

public class LikeNotFoundException extends NotFoundException {

    public LikeNotFoundException() {
        super("Like not found");
    }
}
