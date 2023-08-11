package Funssion.Inforum.domain.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class EmailRequestDto {
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$",message="이메일 주소 양식을 확인해주세요")
    @NotEmpty(message = "이메일을 입력해 주세요")
    private String email;
    public EmailRequestDto(String email) {
        this.email = email;
    }

    public EmailRequestDto() {
    }
}
