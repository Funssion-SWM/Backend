package Funssion.Inforum.domain.post.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnswerSaveDto {
    @NotBlank(message="내용을 입력해주세요")
    private String text;
    @Builder
    public AnswerSaveDto(String text, String description) {
        this.text = text;
    }
}
