package Funssion.Inforum.domain.follow.service;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.follow.domain.Follow;
import Funssion.Inforum.domain.follow.repository.FollowRepository;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import Funssion.Inforum.domain.profile.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static Funssion.Inforum.common.constant.NotificationType.NEW_FOLLOWER;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;
    private final MyRepository myRepository;

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
        sendNotificationToFollowedUser(userIdToFollow, userId);
    }

    private void sendNotificationToFollowedUser(Long receiverId, Long senderId) {
        MemberProfileEntity senderProfile = myRepository.findProfileByUserId(senderId);
        notificationRepository.save(
                Notification.builder()
                        .receiverId(receiverId)
                        .senderId(senderProfile.getUserId())
                        .senderImagePath(senderProfile.getProfileImageFilePath())
                        .senderRank(senderProfile.getRank())
                        .senderName(senderProfile.getNickname())
                        .notificationType(NEW_FOLLOWER)
                        .build()
        );
    }

    @Transactional
    public void unfollow(Long userIdToUnfollow) {
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        followRepository.findByUserIdAndFollowedUserId(userId, userIdToUnfollow)
                .orElseThrow(() -> new BadRequestException("아직 팔로우 하지 않은 유저입니다."));

        followRepository.delete(userId, userIdToUnfollow);

        memberRepository.updateFollowCnt(userId, Sign.MINUS);
        memberRepository.updateFollowerCnt(userIdToUnfollow, Sign.MINUS);
        notificationRepository.deleteFollowNotification(userIdToUnfollow, userId);
    }

    public List<MemberProfileEntity> getFollowingUserList(Long userId) {
        return followRepository.findFollowingProfilesByUserId(userId);
    }

    public List<MemberProfileEntity> getFollowedUserList(Long userId) {
        return followRepository.findFollowedProfilesByUserId(userId);
    }
}
