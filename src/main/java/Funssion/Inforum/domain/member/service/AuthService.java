package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.member.dto.request.NonSocialMemberLoginDto;
import Funssion.Inforum.domain.member.dto.response.TokenDto;
import Funssion.Inforum.domain.member.entity.CustomUserDetails;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.NonSocialMemberRepository;
import Funssion.Inforum.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final NonSocialMemberRepository nonSocialMemberRepository;

    public TokenDto makeTokenInfo(NonSocialMemberLoginDto nonSocialMemberLoginDto){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(nonSocialMemberLoginDto.getUserEmail(), nonSocialMemberLoginDto.getUserPw());
        // authenticate 메소드가 실행이 될 때 CustomUserDetailsService class의 loadUserByUsername 메소드가 실행 및 db와 대조하여 인증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 해당 객체를 SecurityContextHolder에 저장하고
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 인증받은 새로운 authentication 객체를 createToken 메소드를 통해서 JWT Token을 생성
        String jwt = tokenProvider.createToken(authentication);
        return new TokenDto(jwt,Long.parseLong(authentication.getName()));
    }
    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        NonSocialMember member = nonSocialMemberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userEmail: " + userEmail));
        // non social, social 섞어있기 때문에, user_id를 CustomUserDetail 의 id로 생성합니다. -> 토큰의 getName의 user_id가 들어갑니다.
        return new CustomUserDetails(member.getUserId(), member.getUserEmail(), member.getUserPw(), true, false);
    }
}
