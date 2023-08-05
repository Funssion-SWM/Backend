package Funssion.Inforum.domain.member.controller;


import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.CodeCheckDto;
import Funssion.Inforum.domain.member.dto.request.EmailRequestDto;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.isSuccessSendingEmailDto;
import Funssion.Inforum.domain.member.dto.response.ValidatedDto;
import Funssion.Inforum.domain.member.dto.response.ValidMemberDto;
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
        Long savedId = memberService.requestMemberRegistration(memberSaveDto).getId();
        return new ResponseEntity(savedId,HttpStatus.CREATED);
    }

    @PostMapping ("/authenticate-email")
    public isSuccessSendingEmailDto mailSend(@RequestBody @Valid EmailRequestDto emailDto) {
        String decodedEmail = URLDecoder.decode(emailDto.getEmail(), StandardCharsets.UTF_8);
        if (memberService.isValidEmail(decodedEmail, LoginType.NON_SOCIAL).isValid()){
            return mailService.sendEmailCode(emailDto.getEmail());
        }else{
            return new isSuccessSendingEmailDto(false,"이미 등록된 이메일입니다.");
        }
    }
    @PostMapping("/authenticate-code")
    public ValidatedDto AuthCheck(@RequestBody @Valid CodeCheckDto codeCheckDto){
        return mailService.isAuthorizedEmail(codeCheckDto);
    }

    @GetMapping("/check-duplication")
    public ValidatedDto isValidName(@RequestParam(value="name", required=true) String name){
        return memberService.isValidName(name,LoginType.NON_SOCIAL);
    }

    @GetMapping("/check")
    public ValidMemberDto method(@CurrentSecurityContext SecurityContext context) {
        String userId = context.getAuthentication().getName();
        Long loginId = userId.equals("anonymousUser") ? -1L : Long.valueOf(userId);
        boolean isLogin = !userId.equals("anonymousUser");
        return new ValidMemberDto(loginId, isLogin);
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
        ResponseCookie invalidateCookie = ResponseCookie.from("token","none").maxAge(0).path("/").domain(".inforum.me").sameSite("none").httpOnly(true).secure(true).build();
        response.addHeader("Set-Cookie", invalidateCookie.toString());
    }
}