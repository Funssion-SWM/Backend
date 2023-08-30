package Funssion.Inforum.domain.post.comment.dto.response;

import Funssion.Inforum.domain.post.comment.domain.ReComment;

import java.sql.Date;

public class ReCommentListDto {
    private Long id;
    private Long authorId;
    private String authorName;
    private String authorImagePath;
    private Date createdDate;
    private Date updatedDate;
    private Long parentCommentId;
    private Long likes;

    public ReCommentListDto(ReComment reComment) {
        this.id = reComment.getId();
        this.authorId = reComment.getAuthorId();
        this.authorName = reComment.getAuthorName();
        this.authorImagePath = reComment.getAuthorImagePath();
        this.createdDate = reComment.getCreatedDate();
        this.updatedDate = reComment.getUpdatedDate();
        this.parentCommentId = reComment.getParentCommentId();
        this.likes = reComment.getLikes();
    }
}
