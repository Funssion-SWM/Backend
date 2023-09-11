package Funssion.Inforum.domain.post.like.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.post.like.domain.Like;

import java.util.List;

public interface LikeRepository {

    Like create(Like like);
    Like findById(Long id);
    Like findByUserIdAndPostInfo(Long userId, PostType postType, Long postId);
    List<Like> findAllByUserIdAndPostType(Long userId, PostType postType);
    void delete(Long userId, PostType postType, Long postId);
}