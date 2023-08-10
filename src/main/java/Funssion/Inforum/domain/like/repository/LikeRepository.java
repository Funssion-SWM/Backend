package Funssion.Inforum.domain.like.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.like.domain.Like;

import java.util.List;

public interface LikeRepository {

    Like save(Like like);
    Like findById(Long id);
    Like findByUserIdAndPostInfo(Long userId, PostType postType, Long postId);
    List<Like> findAllByUserIdAndPostType(Long userId, PostType postType);
    void delete(Long userId, PostType postType, Long postId);
}
