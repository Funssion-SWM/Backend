package Funssion.Inforum.domain.follow.service;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.follow.domain.Follow;
import Funssion.Inforum.domain.follow.repository.FollowRepository;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void follow(Long userIdToFollow) {
        Long userId = SecurityContextUtils.getAuthorizedUserId();

        followRepository.findByUserIdAndFollowedUserId(userId, userIdToFollow)
                .ifPresent(follow -> {
                    throw new BadRequestException("이미 팔로우 한 유저입니다.");
                });

        followRepository.save(Follow.builder()
                .userId(userId)
                .followedUserId(userIdToFollow)
                .build());

        memberRepository.updateFollowCnt(userId, Sign.PLUS);
        memberRepository.updateFollowerCnt(userIdToFollow, Sign.PLUS);

    }

    @Transactional
    public void unfollow(Long userIdToUnfollow) {
        Long userId = SecurityContextUtils.getAuthorizedUserId();

        followRepository.findByUserIdAndFollowedUserId(userId, userIdToUnfollow)
                .orElseThrow(() -> new BadRequestException("아직 팔로우 하지 않은 유저입니다."));

        followRepository.delete(userId, userIdToUnfollow);

        memberRepository.updateFollowCnt(userId, Sign.MINUS);
        memberRepository.updateFollowerCnt(userIdToUnfollow, Sign.MINUS);

    }
}
