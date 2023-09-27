package Funssion.Inforum.domain.follow.controller;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {
    
    private final FollowService followService;

    @PostMapping("/follow")
    public void followUser(@RequestParam Long userIdToFollow) {
        validateUser(userIdToFollow);
        followService.follow(userIdToFollow);
    }

    private static void validateUser(Long userIdToFollow) {
        Long userId = SecurityContextUtils.getAuthorizedUserId();

        if (userId.equals(userIdToFollow)) 
            throw new BadRequestException("자신을 팔로우 할 수 없습니다.");
    }
}
