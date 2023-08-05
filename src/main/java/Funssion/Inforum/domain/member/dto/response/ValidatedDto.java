package Funssion.Inforum.domain.member.dto.response;

import lombok.Getter;

@Getter
public class ValidatedDto {
    private boolean isValid;
    private String message;
    public ValidatedDto(boolean isValid,String message) {
        this.isValid = isValid;
        this.message = message;
    }
}
