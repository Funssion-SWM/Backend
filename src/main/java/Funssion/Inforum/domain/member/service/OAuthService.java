package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.common.constant.Role;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.CustomUserDetails;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.exception.DuplicateMemberException;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return getCustomUserDetails(oAuth2User, email);
    }

    @NotNull
    public CustomUserDetails getCustomUserDetails(OAuth2User oAuth2User, String email) {
        String nickname = UUID.randomUUID().toString().substring(0,15);

        memberRepository.findNonSocialMemberByEmail(email).ifPresent(m->{
            throw new DuplicateMemberException("일반 회원가입으로 등록된 이메일입니다.");
        });
        Optional<SocialMember> socialMember = memberRepository.findSocialMemberByEmail(email);

        if(socialMember.isEmpty()){
            SocialMember savedSocialMember = SocialMember.createSocialMember(email, nickname);
            SaveMemberResponseDto savedResponse = memberRepository.save(savedSocialMember);
            String roles = Role.addRole(Role.getIncludingRoles(savedResponse.getRole()), Role.OAUTH_FIRST_JOIN);// 최초 회원가입을 위한 임시 role 추가
            return new CustomUserDetails(String.valueOf(savedResponse.getId()), roles, oAuth2User.getAttributes());
        }
        else{
            return new CustomUserDetails(String.valueOf(socialMember.get().getUserId()), Role.getIncludingRoles(socialMember.get().getRole()), oAuth2User.getAttributes());
        }
    }
}
