package Funssion.Inforum.domain.post.utils;

import Funssion.Inforum.domain.profile.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostUtils {
    private final ProfileRepository profileRepository;

}