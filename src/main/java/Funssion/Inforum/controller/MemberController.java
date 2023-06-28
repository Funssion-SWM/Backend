package Funssion.Inforum.controller;

import Funssion.Inforum.entity.member.Member;
import Funssion.Inforum.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }
    @GetMapping("/hi")
    @ResponseBody
    public String check(){
        System.out.println("hi");
        return "hiiii";
    }
    @PostMapping("/users")
    @ResponseBody
    public Long create(@RequestBody Member memberForm){ //dto로 바꿔야함
        Member member = new Member();
        member.setUser_name(memberForm.getUser_name());
        return memberService.join(member);
    }
}
