package Funssion.Inforum.common.converter;

import Funssion.Inforum.common.constant.OrderType;
import org.springframework.core.convert.converter.Converter;

public class OrderTypeConverter implements Converter<String, OrderType> {

    @Override
    public OrderType convert(String source) {
        return OrderType.of(source);
    }
}
