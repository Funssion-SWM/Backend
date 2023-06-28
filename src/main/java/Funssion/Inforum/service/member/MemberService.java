package Funssion.Inforum.service.member;

import Funssion.Inforum.entity.member.Member;
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

    public Long join (Member member){
        //
        memberRepository.save(member);
        return member.getUser_id();
    }
}
