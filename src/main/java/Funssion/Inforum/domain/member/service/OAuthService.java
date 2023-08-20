package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.CustomUserDetails;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.SocialMemberRepository;
import Funssion.Inforum.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final SocialMemberRepository socialMemberRepository;
    private final TokenProvider tokenProvider;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        log.info("oauth2user = {}",oAuth2User);
        String email = oAuth2User.getAttribute("email");
        String nickname = UUID.randomUUID().toString().substring(0,15);
        String password = "default";
//        Role role = Role.ROLE_USER;


        Optional<SocialMember> socialMember = socialMemberRepository.findByEmail(email);


        if(socialMember.isEmpty()){
            SocialMember savedSocialMember = SocialMember.createSocialMember(email, nickname);
            SaveMemberResponseDto savedResponse = socialMemberRepository.save(savedSocialMember);
            User savedUser = new User (String.valueOf(savedResponse.getId()),password,Collections.emptyList());
            return new CustomUserDetails(String.valueOf(savedResponse.getId()),savedUser,oAuth2User.getAttributes());
        }
        else{
            User savedUser = new User (String.valueOf(socialMember.get().getUserId()),password,Collections.emptyList());
            log.info("get name = {}, savedUser = {}, attributes ={}",savedUser.getUsername(),savedUser,oAuth2User.getAttributes());
            return new CustomUserDetails(savedUser.getUsername(),savedUser,oAuth2User.getAttributes());
        }
    }
    public String socialLogin(Authentication authentication, OAuth2User oAuth2UserPrincipal){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // PrincipalOauth2UserService의 getAttributes내용과 같음

        Map<String, Object> attributes1 = oAuth2UserPrincipal.getAttributes();
        // attributes == attributes1

        log.info("social login 중 = code = google");
        return attributes.toString();     //세션에 담긴 user가져올 수 있음음
    }

}
