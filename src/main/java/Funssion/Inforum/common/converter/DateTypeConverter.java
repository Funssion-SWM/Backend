package Funssion.Inforum.common.converter;

import Funssion.Inforum.common.constant.DateType;
import org.springframework.core.convert.converter.Converter;

public class DateTypeConverter implements Converter<String, DateType> {

    @Override
    public DateType convert(String source) {
        return DateType.of(source);
    }
}
