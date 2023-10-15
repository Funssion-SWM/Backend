package Funssion.Inforum.common.constant;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DateType {
    DAY("1 day"), WEEK("1 week"), MONTH("1 month"), YEAR("1 year");

    private final String interval;

    public static DateType of(String period) {
        try {
            return DateType.valueOf(period.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Date Type", e);
        }
    }
}
