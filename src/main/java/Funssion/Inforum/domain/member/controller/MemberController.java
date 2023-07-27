package Funssion.Inforum.domain.member.controller;


import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.EmailCheckDto;
import Funssion.Inforum.domain.member.dto.EmailRequestDto;
import Funssion.Inforum.domain.member.dto.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.ValidDto;
import Funssion.Inforum.domain.member.service.MailService;
import Funssion.Inforum.domain.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    private final MailService mailService;

    @PostMapping("")
    public ResponseEntity create(@RequestBody @Valid MemberSaveDto memberSaveDto) throws NoSuchAlgorithmException { //dto로 바꿔야함
        Long save_id = memberService.join(memberSaveDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }
    @GetMapping("/email-valid")
    public ValidDto isValidEmail(@RequestParam(value="email", required=true) String email){
        String docodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);
        return memberService.isValidEmail(docodedEmail,LoginType.NON_SOCIAL);
    }

    @PostMapping ("/mailAuth")
    public String mailSend(@RequestBody @Valid EmailRequestDto emailDto) {
        System.out.println("이메일 인증 이메일 :" + emailDto.getEmail());
        return mailService.joinEmail(emailDto.getEmail()); //인증번호 String으로 return
    }
    @PostMapping("/mailAuthCheck")
    public ValidDto AuthCheck(@RequestBody @Valid EmailCheckDto emailCheckDto){
        Boolean Checked=mailService.CheckAuthNum(emailCheckDto.getEmail(),emailCheckDto.getAuthNum());
        return new ValidDto(Checked);
    }
    @GetMapping("/name-valid")
    public ValidDto isValidName(@RequestParam(value="name", required=true) String name){
        return memberService.isValidName(name,LoginType.NON_SOCIAL);
    }
    @GetMapping("/check")
    public String checkToken(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    @GetMapping("/logout")
    public void logout(HttpServletResponse response){
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0); // 유효시간을 0으로 설정
        response.addCookie(cookie); // 응답 헤더에 추가해서 없어지도록 함
    }
}