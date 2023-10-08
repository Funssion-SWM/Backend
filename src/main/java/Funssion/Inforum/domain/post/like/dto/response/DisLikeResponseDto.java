package Funssion.Inforum.domain.post.like.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class DisLikeResponseDto {
    private Boolean isLike;
    private Long likes;
}
