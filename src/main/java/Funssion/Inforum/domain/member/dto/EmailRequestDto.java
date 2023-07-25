package Funssion.Inforum.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequestDto {
    @Email
    @NotEmpty(message = "이메일을 입력해 주세요")
    private String email;
}
