package Funssion.Inforum.common.constant.memo;

import Funssion.Inforum.common.exception.BadRequestException;

public enum DateType {
    DAY, WEEK, MONTH, YEAR;

    public static Integer toNumOfDays(DateType type) {
        switch (type) {
            case DAY -> {
                return 1;
            }
            case WEEK -> {
                return 7;
            }
            case MONTH -> {
                return 31;
            }
            case YEAR -> {
                return 365;
            }
        }

        throw new BadRequestException("Invalid Date Type");
    }
}
