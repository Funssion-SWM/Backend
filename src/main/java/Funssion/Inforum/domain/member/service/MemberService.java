package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.MemberSaveForm;
import Funssion.Inforum.domain.member.dto.ValidDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.exception.NotYetImplementException;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/* Spring Security 에서 유저의 정보를 가저오기 위한 로직이 포함. */
@Slf4j
@Service
public class MemberService{
    //생성자로 같은 타입의 클래스(MemberRepository) 다수 조회 후, Map으로 조회
    private final Map<String,MemberRepository> repositoryMap;
    private MemberRepository memberRepository;
    private final MyRepository myRepository;

    public MemberService(Map<String, MemberRepository> repositoryMap,MyRepository myRepository) {
        this.repositoryMap = repositoryMap;
        this.myRepository = myRepository;
        log.info("Different LogintType Supported by Repositories m= {} ", repositoryMap);
    }
    HashMap<LoginType, String> loginTypeMap = new HashMap<>();
    {
        loginTypeMap.put(LoginType.NON_SOCIAL,"nonSocialMemberRepository");
        loginTypeMap.put(LoginType.SOCIAL, "socialMemberRepository");
    }


    @Transactional
    public Long join (MemberSaveForm memberSaveForm) throws NoSuchAlgorithmException {
        Long user_id = -1L;
        LoginType loginType = memberSaveForm.getLoginType();
        log.info("loginType = {}",loginType);

        //중복 처리 한번더 검증
        if(!isValidEmail(memberSaveForm.getUserEmail(),loginType).isValid()){
            throw new IllegalStateException("이미 가입된 회원 이메일입니다.");
        }
        if(! isValidName(memberSaveForm.getUserName(),loginType).isValid()){
            throw new IllegalStateException("이미 가입된 닉네임입니다.");
        }

        memberRepository = repositoryMap.get(loginTypeMap.get(loginType));

        switch (loginType) {
            case NON_SOCIAL:
                NonSocialMember member = new NonSocialMember();
                member.setUserName(memberSaveForm.getUserName());
                member.setLoginType(loginType);
                member.setUserEmail(memberSaveForm.getUserEmail());
                member.setUserPw(memberSaveForm.getUserPw());
                NonSocialMember saveMember = (NonSocialMember) memberRepository.save(member);
                user_id = saveMember.getUserId();
                myRepository.createHistory(user_id.intValue());
                return user_id;
            case SOCIAL: //social 회원가입의 경우 -> 요청 필요
            {
                throw new NotYetImplementException("해당 요청은 아직 구현되지 않았습니다.");
            }
        }
        return user_id; // non valid request, return -1
    }

    public ValidDto isValidName(String username, LoginType loginType) {
        memberRepository = repositoryMap.get(loginTypeMap.get(loginType));
        // findByName 메서드를 호출하고 결과가 존재하는지 확인하여 중복 검사를 수행
        Optional<NonSocialMember> optionalMember = memberRepository.findByName(username);
        if (optionalMember.isPresent()) {
            return new ValidDto(false);
        }
        return new ValidDto(true);
    }
    public ValidDto isValidEmail(String email, LoginType loginType){
        log.info("logintype in emailval= {}", loginType);
        MemberRepository memberRepository1 = repositoryMap.get(loginTypeMap.get(loginType));
        log.info("emailal repo = {}",memberRepository1);
        memberRepository = repositoryMap.get(loginTypeMap.get(loginType));
        Optional<NonSocialMember> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            return new ValidDto(false);
        }
        return new ValidDto(true);
    }

}
