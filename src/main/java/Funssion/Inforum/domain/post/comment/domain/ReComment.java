package Funssion.Inforum.domain.post.comment.domain;

import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.domain.Post;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class ReComment extends Post {
    private Long parentCommentId;
    private String commentText;

    public ReComment(Long authorId, MemberProfileEntity authorProfile, LocalDateTime createdDate, LocalDateTime updatedDate, Long parentCommentId, String commentText) {
        super(authorId,authorProfile,createdDate,updatedDate);
        this.parentCommentId = parentCommentId;
        this.commentText = commentText;
    }

}
