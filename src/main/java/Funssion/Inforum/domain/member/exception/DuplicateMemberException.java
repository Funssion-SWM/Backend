package Funssion.Inforum.domain.member.exception;

import Funssion.Inforum.common.exception.DuplicateException;

public class DuplicateMemberException extends DuplicateException {

    public DuplicateMemberException(String message) {
        super(message);
    }
}
