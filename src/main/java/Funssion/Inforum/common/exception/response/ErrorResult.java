package Funssion.Inforum.common.exception.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@ToString
public class ErrorResult {
    private Integer code;
    private String message;

    public ErrorResult(HttpStatus status, String message) {
        this.code = status.value();
        this.message = message;
    }
}
