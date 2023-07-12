package Funssion.Inforum.swagger.member.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Schema(description="회원가입 Request API")
@Getter @Setter
//DTO 정의
public class MemberSaveForm {
    @NotBlank
    @Schema(description="유저 닉네임", example="염소")
    private String user_name;
    @Schema(description="로그인 유형", defaultValue = "0",allowableValues = {"0","1"})
    private Integer login_type = 0; //default 값은 non-social 로그인 타입인 0 으로 설정

    @NotBlank
    @Email
    @Schema(description="유저 이메일")
    private String user_email; // 소셜 로그인 이메일, 개인 인증 이메일 둘다 user_email에 속함
    @NotBlank
    @Pattern(regexp=("/^(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,20}$/"),message="비밀번호는 특수문자와 숫자가 적어도 하나 포함된 8~20자리로 설정해주세요")
    @Schema(description="유저 비밀번호 (특수문자와 숫자가 적어도 하나 포함된 8~20자리)")
    private String user_pw;

}