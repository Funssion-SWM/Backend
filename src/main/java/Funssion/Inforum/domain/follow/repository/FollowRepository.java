package Funssion.Inforum.domain.follow.repository;

import Funssion.Inforum.domain.follow.domain.Follow;

import java.util.Optional;

public interface FollowRepository {
    void save(Follow follow);
    Optional<Follow> findByUserIdAndFollowId(Long userId, Long followId);
}
