package Funssion.Inforum.common.constant;

import Funssion.Inforum.common.exception.etc.EnumParseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.TypeMismatchException;

@RequiredArgsConstructor
@Getter
public enum Sign {
    PLUS(1), MINUS(-1);

    private final int value;
}
