package Funssion.Inforum.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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