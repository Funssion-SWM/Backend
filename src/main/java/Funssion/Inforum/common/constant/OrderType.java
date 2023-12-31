package Funssion.Inforum.common.constant;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;

public enum OrderType {
    HOT, NEW, ANSWERS, SOLVED;

    public static OrderType of (String orderBy) {
        try {
            return OrderType.valueOf(orderBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order by type", e);
        }
    }
}
