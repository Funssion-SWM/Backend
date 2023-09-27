package Funssion.Inforum.domain.follow.service;

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

        followRepository.findByUserIdAndFollowId(userId, userIdToFollow)
                .ifPresent(follow -> {
                    throw new BadRequestException("이미 팔로우 한 유저입니다.");
                });

        followRepository.save(Follow.builder()
                .userId(userId)
                .followId(userIdToFollow)
                .build());


    }
}
