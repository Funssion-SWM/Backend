package Funssion.Inforum.domain.like.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.like.domain.Like;

public interface LikeRepository {

    Like save(Like like);
    Like findById(Long id);
    Like findByUserIdAndPostInfo(Long userId, PostType postType, Long postId);
    void delete(Long userId, PostType postType, Long postId);
}
