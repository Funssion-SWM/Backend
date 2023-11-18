package Funssion.Inforum.domain.post.qna.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateQuestionDto {
    private Long questionId;
    private String message;
}
