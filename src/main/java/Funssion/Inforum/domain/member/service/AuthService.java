package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.common.constant.Role;
import Funssion.Inforum.domain.member.entity.CustomUserDetails;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final MemberRepositoryImpl nonSocialMemberRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        NonSocialMember member = nonSocialMemberRepository.findNonSocialMemberByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("이 이메일과 매칭되는 유저가 존재하지 않습니다 : " + userEmail));
        // non social, social 섞어있기 때문에, user_id를 CustomUserDetail 의 id로 생성합니다. -> 토큰의 getName의 user_id가 들어갑니다.
        return new CustomUserDetails(member.getUserId(), Role.getIncludingRoles(member.getRole()), member.getUserEmail(), member.getUserPw(), true, false);
    }

}