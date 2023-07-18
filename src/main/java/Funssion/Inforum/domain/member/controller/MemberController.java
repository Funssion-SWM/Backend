package Funssion.Inforum.domain.member.controller;

import Funssion.Inforum.domain.member.dto.NonSocialMemberSaveForm;
import Funssion.Inforum.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입 실행 API",description = "소셜로그인/일반로그인 구분 필수", tags = {"Member"})
    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity create(@RequestBody @Validated NonSocialMemberSaveForm nonSocialMemberSaveForm, BindingResult bindingResult) throws NoSuchAlgorithmException { //dto로 바꿔야함
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        log.info("create in controller, member = {}", nonSocialMemberSaveForm);
        Long save_id = memberService.join(nonSocialMemberSaveForm);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ResponseBody
    public String validateDuplicateEmail(@RequestParam(value="email", required=true) String email){
        NonSocialMemberSaveForm nonSocialMemberSaveForm = new NonSocialMemberSaveForm();
        nonSocialMemberSaveForm.setUser_email(email);
        memberService.validateDuplicateEmail(nonSocialMemberSaveForm, nonSocialMemberSaveForm.getLogin_type());
        return "ok";
    }
    @ResponseBody
    @GetMapping("/check")
    public String checkToken(){
        return "ok";
    }
}