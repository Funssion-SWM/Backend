package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.exception.NotYetImplementException;
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
    private final Map<String,MemberRepository> repositoryMap;
    public MemberService(Map<String, MemberRepository> repositoryMap) {
        this.repositoryMap = repositoryMap;
        log.info("Repository Implementation INFO = {} ", repositoryMap);
    }

    private MemberRepository memberRepository;

    HashMap<Integer, String> loginTypeMap = new HashMap<>();
    {
        loginTypeMap.put(0,"nonSocialMemberRepository");
        loginTypeMap.put(1,"socialMemberRepository");
    }

    public Long join (MemberSaveForm memberSaveForm) throws NoSuchAlgorithmException {
        Long user_id = -1L;
        int login_type = memberSaveForm.getLogin_type();

        //중복 처리 한번더 검증
        validateDuplicateEmail(memberSaveForm,login_type);
        validateDuplicateName(memberSaveForm,login_type);

        memberRepository = repositoryMap.get(login_type);

        switch (login_type) {
            case 0:
                NonSocialMember member = new NonSocialMember();
                member.setUser_name(memberSaveForm.getUser_name());
                member.setUser_email(memberSaveForm.getUser_email());
                member.setUser_pw(memberSaveForm.getUser_pw());
                NonSocialMember saveMember = (NonSocialMember) memberRepository.save(member);
                user_id = saveMember.getUser_id();
                return user_id;
            case 1: //social 회원가입의 경우 -> 요청 필요
            {
                throw new NotYetImplementException("해당 요청은 아직 구현되지 않았습니다.");
            }
        }
        return user_id; // non valid request, return -1
    }

    public void validateDuplicateName(MemberSaveForm memberForm, Integer login_type){
        memberRepository = repositoryMap.get(login_type);
        log.info("check validity of name = {}",memberRepository.findByName("hi"));
        memberRepository.findByName(memberForm.getUser_name()).ifPresent(m->{
            log.info("name check");
            throw new IllegalStateException("이미 존재하는 회원 닉네임입니다.");
        });
    }

    public void validateDuplicateEmail(MemberSaveForm memberForm, Integer login_type){
        memberRepository = repositoryMap.get(login_type);
        memberRepository.findByEmail(memberForm.getUser_email()).ifPresent(m->{
            throw new IllegalStateException("이미 존재하는 회원 이메일입니다.");
        });
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        MemberRepository memberRepository = repositoryMap.get(0);
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
