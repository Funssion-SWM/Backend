package Funssion.Inforum.domain.post.comment.domain;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.domain.Post;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class Comment extends Post {
    private PostType postTypeWithComment;
    @NotEmpty
    private String commentText;
    private Long postId;
    private long replies;

    public Comment(Long authorId, MemberProfileEntity memberProfileEntity, LocalDateTime createdDate, LocalDateTime updatedDate,
                   CommentSaveDto commentSaveDto) {
        super(authorId, memberProfileEntity, createdDate, updatedDate);
        this.postTypeWithComment = commentSaveDto.getPostTypeWithComment();
        this.postId = commentSaveDto.getPostId();
        this.commentText = commentSaveDto.getCommentText();
    }

}
