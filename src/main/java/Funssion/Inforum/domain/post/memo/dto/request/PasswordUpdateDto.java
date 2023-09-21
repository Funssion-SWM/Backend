package Funssion.Inforum.domain.post.memo.dto.request;

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
public class PasswordUpdateDto {
    @NotBlank
    @JsonProperty("user_pw")
    @Pattern(regexp=("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$"),message="비밀번호는 특수문자와 숫자 및 영문자가 적어도 하나 포함된 8~15자리로 설정해주세요")
    private String userPw;
}
