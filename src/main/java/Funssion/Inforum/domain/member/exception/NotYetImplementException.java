package Funssion.Inforum.domain.member.exception;

import Funssion.Inforum.common.exception.NotFoundException;

public class NotYetImplementException extends NotFoundException {
    public NotYetImplementException(String msg){
        super("아직 구현되지 않았습니다. -구현 예정-");
    }
}
