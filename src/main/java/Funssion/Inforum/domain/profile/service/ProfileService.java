package Funssion.Inforum.domain.profile.service;

import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.profile.ProfileRepository;
import Funssion.Inforum.domain.profile.dto.response.UserProfileForEmployer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public List<UserProfileForEmployer> searchUserProfilesForEmployer(String developmentArea, String techStack) {
        return profileRepository.findUserProfilesForEmployer(developmentArea, techStack);
    }
}
