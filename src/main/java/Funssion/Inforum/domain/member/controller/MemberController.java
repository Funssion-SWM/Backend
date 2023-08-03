package Funssion.Inforum.domain.member.controller;


import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.*;
import Funssion.Inforum.domain.member.service.MailService;
import Funssion.Inforum.domain.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
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
    private final MailService mailService;

    @PostMapping("")
    public ResponseEntity create(@RequestBody @Valid MemberSaveDto memberSaveDto) throws NoSuchAlgorithmException { //dto로 바꿔야함
        Long save_id = memberService.requestMemberRegistration(memberSaveDto).getId();
        return new ResponseEntity(save_id,HttpStatus.CREATED);
    }
    @GetMapping("/email-valid")
    public ValidDto isValidEmail(@RequestParam(value="email", required=true) String email){
        String docodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);
        return memberService.isValidEmail(docodedEmail,LoginType.NON_SOCIAL);
    }

    @PostMapping ("/mailAuth")
    public SuccessEmailSendDto mailSend(@RequestBody @Valid EmailRequestDto emailDto) {
        return mailService.sendEmailCode(emailDto.getEmail());
    }
    @PostMapping("/mailAuthCheck")
    public ValidDto AuthCheck(@RequestBody @Valid CodeCheckDto codeCheckDto){
        return new ValidDto(mailService.isAuthorizedEmail(codeCheckDto));
    }
    @GetMapping("/name-valid")
    public ValidDto isValidName(@RequestParam(value="name", required=true) String name){
        return memberService.isValidName(name,LoginType.NON_SOCIAL);
    }
    @GetMapping("/check")
    public ValidMemberDto method(@CurrentSecurityContext SecurityContext context) {
        String userId = context.getAuthentication().getName();

        if (userId.equals("anonymousUser")){
            return new ValidMemberDto(-1L,false);
        }
        else{
            return new ValidMemberDto(Long.valueOf(userId),true);
        }
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    log.info("[Logout] User Id ={},",cookie.getValue());
                }
            }
        }
        ResponseCookie nonCookie = ResponseCookie.from("token","none").maxAge(0).path("/").domain(".inforum.me").sameSite("none").httpOnly(true).secure(true).build();
        response.addHeader("Set-Cookie", nonCookie.toString());
    }
}