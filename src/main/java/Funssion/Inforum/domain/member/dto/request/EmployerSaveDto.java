package Funssion.Inforum.domain.member.dto.request;

import Funssion.Inforum.domain.member.constant.EnumValid;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.constant.NameValid;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerSaveDto {
    @NotBlank
    @NameValid()
    @JsonProperty("user_name")
    private String userName;
    @EnumValid(enumClass = LoginType.class)
    @JsonProperty("login_type")
    private LoginType loginType; //회원가입은 논소셜만 가정

    @NotBlank
    @JsonProperty("user_email")
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$",message="이메일 주소 양식을 확인해주세요")
    private String userEmail; // 소셜 로그인 이메일, 개인 인증 이메일 둘다 user_email에 속함
    @NotBlank
    @JsonProperty("user_pw")
    @Pattern(regexp=("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$"),message="비밀번호는 특수문자와 숫자 및 영문자가 적어도 하나 포함된 8~15자리로 설정해주세요")
    private String userPw;

    @NotBlank
    private String companyName;
}
