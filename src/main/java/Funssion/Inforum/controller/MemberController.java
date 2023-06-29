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
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/users")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @PostMapping("")
    @ResponseBody
    public ResponseEntity create(@RequestBody MemberRequest memberRegisterRequest){ //dto로 바꿔야함
        memberService.join(memberRegisterRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("emailVal")
    @ResponseBody
    public ResponseEntity validateDuplicateEmail(@RequestParam(value="email", required=true) String email){
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setUser_email(email);
        memberService.validateDuplicateEmail(memberRequest);


    }
}
