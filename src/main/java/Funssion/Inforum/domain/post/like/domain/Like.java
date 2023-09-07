package Funssion.Inforum.domain.post.like.domain;

import Funssion.Inforum.common.constant.PostType;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
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
