package Funssion.Inforum.domain.post.qna.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class AppliedQuestionDto {
    private final Long questionId;
    private final LocalDateTime appliedDateTime;
    private final String message;
}
