package Funssion.Inforum.domain.member.dto;

import Funssion.Inforum.domain.member.constant.EnumValid;
import Funssion.Inforum.domain.member.constant.LoginType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
//DTO 정의
public class MemberSaveDto {
    @NotBlank
    @JsonProperty("user_name")
    private String userName;
    @EnumValid(enumClass = LoginType.class)
    @JsonProperty("login_type")
    private LoginType loginType; //회원가입은 논소셜만 가정

    @NotBlank
    @JsonProperty("user_email")
    @Email(message="이메일 주소 양식을 확인해주세요")
    private String userEmail; // 소셜 로그인 이메일, 개인 인증 이메일 둘다 user_email에 속함
    @NotBlank
    @JsonProperty("user_pw")
//    @Pattern(regexp=("^(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,20}$/"),message="비밀번호는 특수문자와 숫자가 적어도 하나 포함된 8~20자리로 설정해주세요")
    private String userPw;

}
