package Funssion.Inforum.domain.follow.controller;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.follow.service.FollowService;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class FollowController {
    
    private final FollowService followService;

    @PostMapping("/follow")
    public void followUser(@RequestParam @Min(1) Long userId) {
        validateUser(userId);
        followService.follow(userId);
    }

    @PostMapping("/unfollow")
    public void unfollowUser(@RequestParam @Min(1) Long userId) {
        validateUser(userId);
        followService.unfollow(userId);
    }

    @GetMapping("/follows")
    public List<MemberProfileEntity> getFollowUsersInfo(@RequestParam @Min(1) Long userId) {
        return followService.getFollowingUserList(userId);
    }

    @GetMapping("/followers")
    public List<MemberProfileEntity> getFollowerUsersInfo(@RequestParam @Min(1) Long userId) {
        return followService.getFollowedUserList(userId);
    }

    private static void validateUser(Long userId) {
        if (SecurityContextUtils.getAuthorizedUserId().equals(userId))
            throw new BadRequestException("자신을 팔로우 또는 팔로우 취소할 수 없습니다.");
    }
}
