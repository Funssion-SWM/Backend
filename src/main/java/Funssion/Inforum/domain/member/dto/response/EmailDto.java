package Funssion.Inforum.domain.member.dto.response;

import lombok.Getter;

@Getter
public class EmailDto {
    private String email;
    private String message;

    public EmailDto(String email, String message) {
        this.email = email;
        this.message = message;
    }
}
