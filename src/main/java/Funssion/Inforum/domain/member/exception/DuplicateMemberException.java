package Funssion.Inforum.domain.member.exception;

import Funssion.Inforum.common.exception.etc.DuplicateException;

public class DuplicateMemberException extends DuplicateException {

    public DuplicateMemberException(String message) {
        super(message);
    }
}
