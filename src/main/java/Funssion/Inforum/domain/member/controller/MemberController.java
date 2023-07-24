package Funssion.Inforum.domain.member.controller;


import Funssion.Inforum.domain.member.LoginType;
import Funssion.Inforum.domain.member.dto.MemberSaveForm;
import Funssion.Inforum.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {

    private final MemberService memberService;


    @PostMapping("")
    public ResponseEntity create(@RequestBody @Validated MemberSaveForm memberSaveForm, BindingResult bindingResult) throws NoSuchAlgorithmException { //dto로 바꿔야함
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        Long save_id = memberService.join(memberSaveForm);
        return new ResponseEntity(HttpStatus.CREATED);
    }
    @GetMapping("/non-social/email-valid/{email}")
    public Boolean isValidateDuplicateEmail(@RequestParam(value="email", required=true) String email){
        return memberService.isValidEmail(email,LoginType.NON_SOCIAL);
    }
    @GetMapping("/non-social/name-valid/{name}")
    public Boolean isValidName(@RequestParam(value="name", required=true) String name){
        return memberService.isValidName(name,LoginType.NON_SOCIAL);

    }
    @ResponseBody
    @GetMapping("/check")
    public String checkToken(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}