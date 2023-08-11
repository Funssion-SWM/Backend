package Funssion.Inforum.domain.like.dto.response;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.like.domain.Like;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class LikeResponseDto {
    private Long id;
    private Long userId;
    private PostType postType;
    private Long postId;
    private Timestamp created;

    public LikeResponseDto(Like like) {
        this.id = like.getId();
        this.userId = like.getUserId();
        this.postType = like.getPostType();
        this.postId = like.getPostId();
        this.created = like.getCreated();
    }
}
