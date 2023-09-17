package Funssion.Inforum.common.constant;

import Funssion.Inforum.common.exception.BadRequestException;

public enum OrderType {
    HOT, NEW;

    public static OrderType of (String orderBy) {
        try {
            return OrderType.valueOf(orderBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order by type", e);
        }
    }
}
