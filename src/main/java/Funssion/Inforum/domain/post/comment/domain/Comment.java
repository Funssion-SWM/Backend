package Funssion.Inforum.domain.post.comment.domain;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.domain.Post;
import lombok.Getter;

import java.sql.Date;

@Getter
public class Comment extends Post {
    private PostType postTypeWithComment;
    private String commentText;
    private Long postId;
    private long replies;

    public Comment(Long authorId, MemberProfileEntity memberProfileEntity,Date createdDate, Date updatedDate,
                    CommentSaveDto commentSaveDto) {
        super(authorId, memberProfileEntity, createdDate, updatedDate);
        this.postTypeWithComment = commentSaveDto.getPostTypeWithComment();
        this.postId = commentSaveDto.getPostId();
        this.commentText = commentSaveDto.getCommentText();
    }


}
