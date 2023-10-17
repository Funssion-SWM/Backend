package Funssion.Inforum.config;

import Funssion.Inforum.common.converter.BooleanConverter;
import Funssion.Inforum.common.converter.DateTypeConverter;
import Funssion.Inforum.common.converter.OrderTypeConverter;
import Funssion.Inforum.common.converter.PostTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new DateTypeConverter());
        registry.addConverter(new OrderTypeConverter());
        registry.addConverter(new PostTypeConverter());
        registry.addConverter(new BooleanConverter());
    }
}
