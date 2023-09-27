package Funssion.Inforum.common.constant;

import Funssion.Inforum.common.exception.etc.EnumParseException;
import org.springframework.beans.TypeMismatchException;

public enum Sign {
    PLUS, MINUS;

    public static int parseInt(Sign sign) {
        switch (sign) {
            case PLUS -> {
                return 1;
            }
            case MINUS -> {
                return -1;
            }
        }
        throw new EnumParseException();
    }
}
