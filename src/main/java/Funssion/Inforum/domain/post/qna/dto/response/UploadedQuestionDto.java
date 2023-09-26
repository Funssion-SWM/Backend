package Funssion.Inforum.domain.post.qna.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UploadedQuestionDto {
    private final Long questionId;
    private final String message;
}
