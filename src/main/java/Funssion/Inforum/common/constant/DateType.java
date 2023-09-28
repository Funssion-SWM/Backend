package Funssion.Inforum.common.constant;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;

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



    public static DateType of(String period) {
        try {
            return DateType.valueOf(period.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Date Type", e);
        }
    }
}
