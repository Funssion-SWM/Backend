package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.member.entity.CustomUserDetails;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.SocialMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthService extends DefaultOAuth2UserService {
    private final SocialMemberRepository socialMemberRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        String nickname = "default";
        String password = "default";
//        Role role = Role.ROLE_USER;

        Optional<SocialMember> socialMember = socialMemberRepository.findByEmail(email);

        if(socialMember.isEmpty()){
            SocialMember savedSocialMember = SocialMember.createSocialMember(email, nickname);
            socialMemberRepository.save(savedSocialMember);
            return new CustomUserDetails(savedSocialMember,oAuth2User.getAttributes());
        }
        else{
            return new CustomUserDetails(socialMember.get(),oAuth2User.getAttributes());
        }
    }

}
