package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.member.exception.NotYetImplementException;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.MemberSaveForm;
import Funssion.Inforum.domain.member.entity.CustomUserDetails;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/* Spring Security 에서 유저의 정보를 가저오기 위한 로직이 포함. */
@Slf4j
@Service
public class MemberService implements UserDetailsService {
    //생성자로 같은 타입의 클래스(MemberRepository) 다수 조회 후, Map으로 조회
    private final Map<String,MemberRepository> repositoryMap;

    public MemberService(Map<String, MemberRepository> repositoryMap) {
        this.repositoryMap = repositoryMap;
        log.info("Repository Implementation INFO = {} ", repositoryMap);
    }
    HashMap<LoginType, String> loginTypeMap = new HashMap<>();
    {
        loginTypeMap.put(LoginType.NON_SOCIAL,"nonSocialMemberRepository");
        loginTypeMap.put(LoginType.SOCIAL, "socialMemberRepository");
    }

    private MemberRepository memberRepository;

    public Long join (MemberSaveForm memberSaveForm) throws NoSuchAlgorithmException {
        Long user_id = -1L;
        LoginType loginType = memberSaveForm.getLogin_type();
        log.info("loginType = {}",loginType);

        //중복 처리 한번더 검증
        if(! isValidEmail(memberSaveForm.getUser_email(),loginType)){
            throw new IllegalStateException("이미 가입된 회원 이메일입니다.");
        }
        if(! isValidName(memberSaveForm.getUser_name(),loginType)){
            throw new IllegalStateException("이미 가입된 닉네임입니다.");
        }

        memberRepository = repositoryMap.get(loginTypeMap.get(loginType));

        switch (loginType) {
            case NON_SOCIAL:
                NonSocialMember member = new NonSocialMember();
                member.setUser_name(memberSaveForm.getUser_name());
                member.setUser_email(memberSaveForm.getUser_email());
                member.setUser_pw(memberSaveForm.getUser_pw());
                NonSocialMember saveMember = (NonSocialMember) memberRepository.save(member);
                user_id = saveMember.getUser_id();
                return user_id;
            case SOCIAL: //social 회원가입의 경우 -> 요청 필요
            {
                throw new NotYetImplementException("해당 요청은 아직 구현되지 않았습니다.");
            }
        }
        return user_id; // non valid request, return -1
    }

    public boolean isValidName(String username, LoginType loginType) {
        memberRepository = repositoryMap.get(loginTypeMap.get(loginType));
        // findByName 메서드를 호출하고 결과가 존재하는지 확인하여 중복 검사를 수행
        Optional<NonSocialMember> optionalMember = memberRepository.findByName(username);
        if (optionalMember.isPresent()) {
            return false;
        }
        return true;
    }
    public boolean isValidEmail(String email, LoginType loginType){
        log.info("logintype in emailval= {}", loginType);
        MemberRepository memberRepository1 = repositoryMap.get(loginTypeMap.get(loginType));
        log.info("emailal repo = {}",memberRepository1);
        memberRepository = repositoryMap.get(loginTypeMap.get(loginType));
        Optional<NonSocialMember> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            return false;
        }
        return true;
    }
    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        MemberRepository memberRepository = repositoryMap.get(loginTypeMap.get(LoginType.NON_SOCIAL));
        Optional<NonSocialMember> nonSocialMember = memberRepository.findByEmail(userEmail);
        if (nonSocialMember.isPresent()) {
            NonSocialMember member = nonSocialMember.get();
            log.info("member info in loadByUsername method = {}", member.getAuth_id());
            //non social, social 섞어있기 때문에, user_id를 CustomUserDetail 의 id로 생성합니다. ->토큰의 getName의 user_id가 들어갑니다.
            return new CustomUserDetails(member.getUser_id(),member.getUser_email(),member.getUser_pw(),true,false );
        } else {
            throw new UsernameNotFoundException("User not found with userEmail: " + userEmail);
        }
    }

}
