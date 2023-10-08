package Funssion.Inforum.domain.post.like.exception;

import Funssion.Inforum.common.exception.notfound.NotFoundException;

public class DisLikeNotFoundException extends NotFoundException {
    public DisLikeNotFoundException() {
        super("DisLike not found");
    }
}

