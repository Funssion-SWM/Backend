package Funssion.Inforum.domain.post.comment.dto.request;

import Funssion.Inforum.common.constant.PostType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CommentSaveDto {
    private PostType postTypeWithComment;
    private Long postId;
    @NotEmpty
    private String commentText;

}
