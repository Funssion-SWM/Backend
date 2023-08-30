package Funssion.Inforum.domain.post.comment.dto.response;

import Funssion.Inforum.domain.post.comment.domain.Comment;
import lombok.Getter;

import java.sql.Date;

@Getter
public class CommentListDto {
    private Long Id;
    private Long authorId;
    private String authorName;
    private String authorImagePath;
    private Date createdDate;
    private Date updatedDate;
    private Long postId;
    private Long likes;
    private Long replies;

    public CommentListDto(Comment comment) {
        this.Id = comment.getId();
        this.authorId = comment.getAuthorId();
        this.authorName = comment.getAuthorName();
        this.authorImagePath = comment.getAuthorImagePath();
        this.createdDate = comment.getCreatedDate();
        this.updatedDate = comment.getUpdatedDate();
        this.postId = comment.getPostId();
        this.likes = comment.getLikes();
        this.replies = comment.getReplies();
    }
}
