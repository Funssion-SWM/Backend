package Funssion.Inforum.domain.post.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.score.Rank;

public interface PostRepository {
    Long findAuthorId(PostType postType, Long postId);
    void updateRankOfAllPostTypeAndNotification(Rank updateRank, Long userId);
}
