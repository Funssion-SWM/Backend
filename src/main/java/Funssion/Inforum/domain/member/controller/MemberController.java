package Funssion.Inforum.domain.member.controller;

import Funssion.Inforum.domain.member.service.MemberService;
import Funssion.Inforum.domain.member.dto.MemberSaveForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("")
    @ResponseBody
    public ResponseEntity create(@RequestBody @Validated MemberSaveForm memberSaveForm, BindingResult bindingResult){ //dto로 바꿔야함
        log.info("create in controller, member = {}", memberSaveForm);
        memberService.join(memberSaveForm);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("emailVal")
    @ResponseBody
    public String validateDuplicateEmail(@RequestParam(value="email", required=true) String email){
        MemberSaveForm memberSaveForm = new MemberSaveForm();
        memberSaveForm.setUser_email(email);
        memberService.validateDuplicateEmail(memberSaveForm, memberSaveForm.getLogin_type());
        return "ok";
    }
}
