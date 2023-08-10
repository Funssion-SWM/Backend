package Funssion.Inforum.domain.like.domain;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.like.dto.request.LikeSaveDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class Like {
    private Long id;
    private Long userId;
    private PostType postType;
    private Long postId;
    private Timestamp created;

    public Like(Long userId, PostType postType, Long postId) {
        this.userId = userId;
        this.postType = postType;
        this.postId = postId;
    }
}
