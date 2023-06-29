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

        //중복 처리 한번더 검증
        validateDuplicateEmail(memberRegisterRequest);
        validateDuplicateName(memberRegisterRequest);

        //로그인 타입별 다른 회원가입 로직
        switch(memberRegisterRequest.getType()) {
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

    public void validateDuplicateName(MemberRequest memberRequest){
        memberRepository.findByName(memberRequest.getUser_name()).ifPresent(m->{
            throw new IllegalStateException("이미 존재하는 회원 닉네임입니다.");
        });
    }

    public void validateDuplicateEmail(MemberRequest memberRequest){
        memberRepository.findByEmail(memberRequest.getUser_email()).ifPresent(m->{
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        });
    }
}
