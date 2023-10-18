package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.exception.etc.UpdateFailException;

public class ScoreUpdateFailException extends UpdateFailException {
    public ScoreUpdateFailException(String message) {
        super(message);
    }
}
