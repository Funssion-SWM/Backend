package Funssion.Inforum.domain.post.like.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeResponseDto {
    private Boolean isLike;
    private Long likes;
}
