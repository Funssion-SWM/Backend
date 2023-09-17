package Funssion.Inforum.common.converter;

import Funssion.Inforum.common.constant.PostType;
import org.springframework.core.convert.converter.Converter;

public class PostTypeConverter implements Converter<String, PostType> {

    @Override
    public PostType convert(String source) {
        return PostType.of(source.substring(0, source.length() - 1));
    }
}
