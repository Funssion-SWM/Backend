package Funssion.Inforum.controller;

import Funssion.Inforum.dto.member.MemberRequest;
import Funssion.Inforum.entity.member.Member;
import Funssion.Inforum.entity.member.NonSocialMember;
import Funssion.Inforum.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity create(@RequestBody MemberRequest memberRegisterRequest){ //dto로 바꿔야함
        memberService.join(memberRegisterRequest);
        return new ResponseEntity(HttpStatus.OK);
    }
}
