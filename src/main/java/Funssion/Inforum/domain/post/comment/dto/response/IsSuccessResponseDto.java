package Funssion.Inforum.domain.post.comment.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class IsSuccessResponseDto {
    private final Boolean isSuccess;
    private final String message;
}
