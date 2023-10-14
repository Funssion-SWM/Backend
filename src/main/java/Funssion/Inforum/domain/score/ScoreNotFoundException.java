package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.exception.notfound.NotFoundException;

public class ScoreNotFoundException extends NotFoundException {
    public ScoreNotFoundException(String message) {
        super("Score history not found");
    }
}
