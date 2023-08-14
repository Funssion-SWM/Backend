package Funssion.Inforum.common.exception.notfound;

import lombok.Getter;

@Getter
public class UrlNotFoundException extends NotFoundException{

    public UrlNotFoundException() {
        super("url not found");
    }
}
