package Funssion.Inforum.domain.member.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SuccessResponse {
    @Schema(description="HTTP 상태 코드", example="200")
    private Integer statusCode;
    @Schema(description="성공 메시지",example="어떤 작업이 성공했습니다.")
    private String successMessage;
}