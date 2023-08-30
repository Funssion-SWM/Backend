package Funssion.Inforum.domain.post.comment.domain;

import Funssion.Inforum.domain.post.domain.Post;
import lombok.Getter;

@Getter
public class ReComment extends Post {
    private Long parentCommentId;

    public ReComment(Long commentId){
        this.parentCommentId = commentId;
    }
}
