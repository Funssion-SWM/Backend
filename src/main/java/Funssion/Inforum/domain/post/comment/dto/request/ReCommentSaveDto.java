package Funssion.Inforum.domain.post.comment.dto.request;

import Funssion.Inforum.domain.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReCommentSaveDto extends Post {
    private Long parentCommentId;
    private String commentText;
}
