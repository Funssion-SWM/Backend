package Funssion.Inforum.domain.member.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NonSocialMemberLoginDto {
    @NotBlank
    @Email(message="이메일 주소 양식을 확인해주세요")
    @JsonProperty("user_email")
    private String userEmail; // 소셜 로그인 이메일, 개인 인증 이메일 둘다 user_email에 속함
    @NotBlank
    @JsonProperty("user_pw")
    private String userPw;

}
