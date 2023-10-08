package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.CustomUserDetails;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        String nickname = UUID.randomUUID().toString().substring(0,15);
        String password = "default";
//        Role role = Role.ROLE_USER;

        Optional<SocialMember> socialMember = memberRepository.findSocialMemberByEmail(email);

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        if(socialMember.isEmpty()){
            SocialMember savedSocialMember = SocialMember.createSocialMember(email, nickname);
            SaveMemberResponseDto savedResponse = memberRepository.save(savedSocialMember);
            authorities.add(new SimpleGrantedAuthority("ROLE_FIRST_JOIN"));
            User savedUser = new User (String.valueOf(savedResponse.getId()),password,authorities);
            return new CustomUserDetails(String.valueOf(savedResponse.getId()),authorities,savedUser,oAuth2User.getAttributes());
        }
        else{
            authorities.add(new SimpleGrantedAuthority("ROLE_EXIST_USER"));
            User savedUser = new User (String.valueOf(socialMember.get().getUserId()),password,authorities);
            log.info("check for signin");
            return new CustomUserDetails(savedUser.getUsername(),authorities,savedUser,oAuth2User.getAttributes());
        }
    }
}
