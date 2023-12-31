package Funssion.Inforum.domain.post.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.mypage.domain.ScoreAndCountDao;
import Funssion.Inforum.domain.score.Rank;

public interface PostRepository {
    Long findAuthorId(PostType postType, Long postId);
    void updateRankOfAllPostTypeAndNotification(Rank updateRank, Long userId);
    boolean isRankUpdateAllPost(Rank updatedRank, Long userId);
    ScoreAndCountDao getAllPostScoreAndCount(Long userId);

}
