package Funssion.Inforum.domain.interview.exception;

import Funssion.Inforum.common.exception.forbidden.ForbiddenException;

public class InterviewForbiddenException extends ForbiddenException {
    public InterviewForbiddenException() {
        super("해당 유저에게 할당된 인터뷰가 아닙니다.");
    }
}
