package Funssion.Inforum.domain.follow.repository;

import Funssion.Inforum.domain.follow.domain.Follow;

import java.util.Optional;

public interface FollowRepository {
    void save(Follow follow);
    void delete(Long userId, Long followedUserId);
    Optional<Follow> findByUserIdAndFollowedUserId(Long userId, Long followedUserId);
}
