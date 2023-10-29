package Funssion.Inforum.domain.profile.controller;

import Funssion.Inforum.common.constant.Role;
import Funssion.Inforum.common.exception.forbidden.ForbiddenException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.profile.dto.response.UserProfileForEmployer;
import Funssion.Inforum.domain.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static Funssion.Inforum.common.constant.Role.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public List<UserProfileForEmployer> searchUserProfilesForEmployer(
            @RequestParam String developmentArea,
            @RequestParam String techStack
    ) {
        return profileService.searchUserProfilesForEmployer(developmentArea, techStack);
    }
}
