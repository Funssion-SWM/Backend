package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.exception.NotYetImplementException;
import Funssion.Inforum.domain.member.dto.NonSocialMemberSaveForm;
import Funssion.Inforum.domain.member.entity.CustomUserDetails;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.NonSocialMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/* Spring Security 에서 유저의 정보를 가저오기 위한 로직이 포함. */
@Slf4j
@Service
@RequiredArgsConstructor
public class NonSocialMemberService implements UserDetailsService {
    private final NonSocialMemberRepository nonSocialmemberRepository;

    public Long join (NonSocialMemberSaveForm nonSocialMemberSaveForm) throws NoSuchAlgorithmException {
        int login_type = nonSocialMemberSaveForm.getLogin_type();
        //중복 처리 한번더 검증
        validateDuplicateEmail(nonSocialMemberSaveForm.getUser_email());
        validateDuplicateName(nonSocialMemberSaveForm.getUser_name());
        Long user_id = -1L;

        //로그인 타입별 다른 회원가입 로직
        switch(login_type) {
            case 0: // non-social 회원가입의 경우
            {
                NonSocialMember member = new NonSocialMember(); // DAO (Entity)로 바꾸는 작업
                member.setUser_name(nonSocialMemberSaveForm.getUser_name());
                member.setUser_email(nonSocialMemberSaveForm.getUser_email());
                member.setUser_pw(nonSocialMemberSaveForm.getUser_pw());
                NonSocialMember saveMember = nonSocialmemberRepository.save(member);
                user_id = saveMember.getUser_id();
                return user_id;
            }
            case 1: //social 회원가입의 경우 -> 요청 필요
            {
                throw new NotYetImplementException("해당 요청은 아직 구현되지 않았습니다.");
            }
        }
        return user_id; // non valid request, return -1
    }

    public void validateDuplicateName(String username){
        nonSocialmemberRepository.findByName(username).ifPresent(m->{
            log.info("name check");
            throw new IllegalStateException("이미 존재하는 회원 닉네임입니다.");
        });
    }

    public void validateDuplicateEmail(String email){
        nonSocialmemberRepository.findByEmail(email).ifPresent(m->{
            throw new IllegalStateException("이미 존재하는 회원 이메일입니다.");
        });
    }
    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Optional<NonSocialMember> nonSocialMember = nonSocialmemberRepository.findByEmail(userEmail);

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
