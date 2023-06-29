package Funssion.Inforum.service.member;

import Funssion.Inforum.dto.member.MemberRequest;
import Funssion.Inforum.entity.member.NonSocialMember;
import Funssion.Inforum.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public void join (MemberRequest memberRegisterRequest){
        //로그인 타입별 다른 회원가입 로직
        switch(memberRegisterRequest.getType()){
            case 0: // non-social 회원가입의 경우
            {
                NonSocialMember member = new NonSocialMember(); //DTO를 DAO로 변환
                member.setUser_name(memberRegisterRequest.getUser_name());
                member.setUser_email(memberRegisterRequest.getUser_email());
                member.setUser_pwd(memberRegisterRequest.getUser_pwd());
                memberRepository.save(member);
            }
        }


    }
}
