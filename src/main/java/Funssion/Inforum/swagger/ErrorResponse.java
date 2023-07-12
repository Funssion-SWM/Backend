package Funssion.Inforum.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ErrorResponse {
    @Schema(description="HTTP 상태 코드", example="400")
    private Integer errorCode;
    @Schema(description="에러 메시지",example="어떤 어떤 오류가 발생했습니다.")
    private String errorMessage;
}
