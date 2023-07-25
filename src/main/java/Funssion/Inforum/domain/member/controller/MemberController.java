package Funssion.Inforum.domain.member.controller;


import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.MemberSaveForm;
import Funssion.Inforum.domain.member.dto.ValidDto;
import Funssion.Inforum.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity create(@RequestBody @Valid MemberSaveForm memberSaveForm) throws NoSuchAlgorithmException { //dto로 바꿔야함
        Long save_id = memberService.join(memberSaveForm);
        return new ResponseEntity(HttpStatus.CREATED);
    }
    @GetMapping("/email-valid")
    public ValidDto isValidEmail(@RequestParam(value="email", required=true) String email){
        String docodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);
        return memberService.isValidEmail(docodedEmail,LoginType.NON_SOCIAL);
    }
    @GetMapping("/name-valid")
    public ValidDto isValidName(@RequestParam(value="name", required=true) String name){
        return memberService.isValidName(name,LoginType.NON_SOCIAL);
    }
    @ResponseBody
    @GetMapping("/check")
    public String checkToken(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}