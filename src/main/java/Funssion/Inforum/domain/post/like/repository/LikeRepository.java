package Funssion.Inforum.domain.post.like.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.post.like.domain.DisLike;
import Funssion.Inforum.domain.post.like.domain.Like;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {

    Like create(Like like);
    DisLike createDisLike(DisLike disLike);
    Like findById(Long id);
    DisLike findByIdOfDisLike(Long id);
    Optional<Like> findByUserIdAndPostInfo(Long userId, PostType postType, Long postId);
    Optional<DisLike> findByUserIdAndPostInfoOfDisLike(Long userId, PostType postType, Long postId);

    List<Like> findAllByUserIdAndPostType(Long userId, PostType postType);
    void deleteLike(Long userId, PostType postType, Long postId);
    void deleteDisLike(Long userId, PostType postType, Long postId);
}
