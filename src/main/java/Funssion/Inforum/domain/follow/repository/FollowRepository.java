package Funssion.Inforum.domain.follow.repository;

import Funssion.Inforum.domain.follow.domain.Follow;
import Funssion.Inforum.domain.member.entity.Member;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;

import java.util.List;
import java.util.Optional;

public interface FollowRepository {
    void save(Follow follow);
    void delete(Long userId, Long followedUserId);
    Optional<Follow> findByUserIdAndFollowedUserId(Long userId, Long followedUserId);
    List<MemberProfileEntity> findFollowingProfilesByUserId(Long userId);
    List<MemberProfileEntity> findFollowedProfilesByUserId(Long userId);
}
