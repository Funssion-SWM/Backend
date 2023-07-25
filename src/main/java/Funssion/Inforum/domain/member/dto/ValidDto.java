package Funssion.Inforum.domain.member.dto;

import lombok.Getter;

@Getter
public class ValidDto {
    private boolean isValid;

    public ValidDto(boolean isValid) {
        this.isValid = isValid;
    }
}
