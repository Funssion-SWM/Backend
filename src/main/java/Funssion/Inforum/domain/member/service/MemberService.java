package Funssion.Inforum.domain.member.service;

import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.ValidDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.exception.NotYetImplementException;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.member.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/* Spring Security 에서 유저의 정보를 가저오기 위한 로직이 포함. */
@Slf4j
@Service
public class MemberService {
    //생성자로 같은 타입의 클래스(MemberRepository) 다수 조회 후, Map으로 조회
    private final Map<String,MemberRepository> repositoryMap;
    private final MyRepository myRepository;

    public MemberService(Map<String, MemberRepository> repositoryMap,MyRepository myRepository) {
        this.repositoryMap = repositoryMap;
        this.myRepository = myRepository;
    }
    HashMap<LoginType, String> loginTypeMap = new HashMap<>();
    {
        loginTypeMap.put(LoginType.NON_SOCIAL,"nonSocialMemberRepository");
        loginTypeMap.put(LoginType.SOCIAL, "socialMemberRepository");
    }



    @Transactional
    public SaveMemberResponseDto requestMemberRegistration (MemberSaveDto memberSaveDto){
        LoginType loginType = memberSaveDto.getLoginType();
        log.debug("Save Member Email = {}, loginType = {}",memberSaveDto.getUserEmail(), loginType);
        //중복 처리 한번더 검증
        if(!isValidEmail(memberSaveDto.getUserEmail(),loginType).isValid()){
            throw new IllegalStateException("이미 가입된 회원 이메일입니다.");
        }
        if(!isValidName(memberSaveDto.getUserName(),loginType).isValid()){
            throw new IllegalStateException("이미 가입된 닉네임입니다.");
        }

        switch (loginType) {
            case NON_SOCIAL:
                MemberRepository selectedMemberRepository = repositoryMap.get(loginTypeMap.get(loginType));
                NonSocialMember member = NonSocialMember.createNonSocialMember(memberSaveDto);
                SaveMemberResponseDto savedMember = selectedMemberRepository.save(member);
                myRepository.createHistory(savedMember.getId());
                return savedMember;
            case SOCIAL: //social 회원가입의 경우 -> 요청 필요
            {
                throw new NotYetImplementException("해당 요청은 아직 구현되지 않았습니다.");
            }
        }
        throw new InvalidParameterException("!~ 수정");
    }

    public ValidDto isValidName(String username, LoginType loginType) {
        MemberRepository selectedMemberRepository = getMemberRepository(loginType);
        log.debug("selected repository = {}",selectedMemberRepository);
        Optional<NonSocialMember> optionalMember = selectedMemberRepository.findByName(username);
        return new ValidDto(optionalMember.isEmpty());
    }
    public ValidDto isValidEmail(String email, LoginType loginType){
        MemberRepository selectedMemberRepository = getMemberRepository(loginType);
        log.debug("selected repository = {}",selectedMemberRepository);
        Optional<NonSocialMember> optionalMember = selectedMemberRepository.findByEmail(email);
        return new ValidDto(optionalMember.isEmpty());
    }


    private MemberRepository getMemberRepository(LoginType loginType) {
        MemberRepository selectedMemberRepository = repositoryMap.get(loginTypeMap.get(loginType));
        return selectedMemberRepository;
    }
}
