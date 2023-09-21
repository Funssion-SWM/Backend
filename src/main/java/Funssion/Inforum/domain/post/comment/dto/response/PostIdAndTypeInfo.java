package Funssion.Inforum.domain.post.comment.dto.response;

import Funssion.Inforum.common.constant.PostType;
import lombok.Getter;

@Getter
public class PostIdAndTypeInfo{
    private final Long postId;
    private final PostType postType;

    public PostIdAndTypeInfo(Long postId, PostType postType) {
        this.postId = postId;
        this.postType = postType;
    }
}
