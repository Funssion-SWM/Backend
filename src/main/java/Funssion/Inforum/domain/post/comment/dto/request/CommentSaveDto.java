package Funssion.Inforum.domain.post.comment.dto.request;

import Funssion.Inforum.common.constant.PostType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentSaveDto {
    private PostType postTypeWithComment;
    private Long postId;
    @NotNull
    private String commentText;

}
