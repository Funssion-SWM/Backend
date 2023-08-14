package Funssion.Inforum.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CodeCheckDto {
    @Email
    @NotEmpty(message = "이메일을 입력해 주세요")
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$",message="이메일 주소 양식을 확인해주세요")
    private String email;

    @NotEmpty(message = "인증 번호를 입력해 주세요")
    private String code;

    @Override
    public boolean equals(Object o){
        if(this ==o) return true;
        if(o == null || this.getClass()!= o.getClass()) return false;
        CodeCheckDto codeCheckDto = (CodeCheckDto) o;
        return Objects.equals(email,codeCheckDto.email) && Objects.equals(code,codeCheckDto.code);
    }
}