package Funssion.Inforum.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonSocialMemberLoginForm {


    @NotBlank
    @Email(message="이메일 주소 양식을 확인해주세요")
    private String user_email; // 소셜 로그인 이메일, 개인 인증 이메일 둘다 user_email에 속함
    @NotBlank
//    @Pattern(regexp=("^(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,20}$/"),message="비밀번호는 특수문자와 숫자가 적어도 하나 포함된 8~20자리로 설정해주세요")
    private String user_pw;

}
