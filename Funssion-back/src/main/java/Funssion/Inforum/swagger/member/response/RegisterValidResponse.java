package Funssion.Inforum.swagger.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterValidResponse{
    @Schema(description="유저 고유 ID", example="1")
    private Integer user_id;
}
