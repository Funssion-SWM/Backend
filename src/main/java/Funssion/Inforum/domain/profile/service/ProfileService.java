package Funssion.Inforum.domain.profile.service;

import Funssion.Inforum.domain.profile.TechStackDto;
import Funssion.Inforum.domain.profile.repository.ProfileRepository;
import Funssion.Inforum.domain.profile.dto.response.UserProfileForEmployer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public List<UserProfileForEmployer> searchUserProfilesForEmployer(TechStackDto techStackDto) {
        return profileRepository.findUserProfilesForEmployer(techStackDto);
    }
}
