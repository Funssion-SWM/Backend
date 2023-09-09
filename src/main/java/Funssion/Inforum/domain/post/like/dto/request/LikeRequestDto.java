package Funssion.Inforum.domain.post.like.dto.request;

import Funssion.Inforum.common.constant.PostType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeRequestDto {

    @NotNull
    private Long userId;
    private PostType postType;
    private Long postId;
}
