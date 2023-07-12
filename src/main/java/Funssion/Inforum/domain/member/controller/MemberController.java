package Funssion.Inforum.domain.member.controller;

import Funssion.Inforum.domain.member.service.MemberService;
import Funssion.Inforum.domain.member.dto.MemberRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Slf4j
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
        log.info("check");
        memberService.join(memberRegisterRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("emailVal")
    @ResponseBody
    public String validateDuplicateEmail(@RequestParam(value="email", required=true) String email){
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setUser_email(email);
        memberService.validateDuplicateEmail(memberRequest);
        return "ok";
    }
}
