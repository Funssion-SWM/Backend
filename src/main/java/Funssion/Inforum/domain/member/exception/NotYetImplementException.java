package Funssion.Inforum.domain.member.exception;

import Funssion.Inforum.common.exception.NotFoundException;

public class NotYetImplementException extends NotFoundException {
    public NotYetImplementException(String msg){
        super(msg);
    }
}
