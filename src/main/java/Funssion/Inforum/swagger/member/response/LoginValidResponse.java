package Funssion.Inforum.swagger.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class LoginValidResponse {
    @Schema(description="유저 고유 식별자 ID")
    private Integer user_id;
    @Schema(description="액세스 토큰")
    private String access_token;
    @Schema(description="리프레시 토큰")
    private String refresh_token;
}
