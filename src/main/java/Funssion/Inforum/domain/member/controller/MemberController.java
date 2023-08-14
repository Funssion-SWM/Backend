package Funssion.Inforum.domain.member.controller;


import Funssion.Inforum.common.exception.BadRequestException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.CodeCheckDto;
import Funssion.Inforum.domain.member.dto.request.EmailRequestDto;
import Funssion.Inforum.domain.member.dto.request.MemberInfoDto;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.*;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.service.MailService;
import Funssion.Inforum.domain.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.Optional;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;


    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public SaveMemberResponseDto create(@RequestBody @Valid MemberSaveDto memberSaveDto) throws NoSuchAlgorithmException { //dto로 바꿔야함
        return memberService.requestMemberRegistration(memberSaveDto);
    }

    @PostMapping("/authenticate-email")
    public IsSuccessResponseDto mailSend(@RequestBody @Valid EmailRequestDto emailDto) {
        String decodedEmail = URLDecoder.decode(emailDto.getEmail(), StandardCharsets.UTF_8);
        if (memberService.isValidEmail(decodedEmail, LoginType.NON_SOCIAL).isValid()) {
            return mailService.sendEmailCode(emailDto.getEmail());
        } else {
            return new IsSuccessResponseDto(false, "이미 등록된 이메일입니다.");
        }
    }

    @PostMapping("/authenticate-code")
    public ValidatedDto AuthCheck(@RequestBody @Valid CodeCheckDto codeCheckDto) {
        return mailService.isAuthorizedEmail(codeCheckDto);
    }

    @GetMapping("/check-duplication")
    public ValidatedDto isValidName(@RequestParam(value = "name", required = true) String name) {
        return memberService.isValidName(name, LoginType.NON_SOCIAL);
    }

    @GetMapping("/check")
    public ValidMemberDto method(@CurrentSecurityContext SecurityContext context) {
        String userId = context.getAuthentication().getName();
        Long loginId = userId.equals("anonymousUser") ? -1L : Long.valueOf(userId);
        boolean isLogin = !userId.equals("anonymousUser");
        return new ValidMemberDto(loginId, isLogin);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    log.info("[Logout] User Id ={},", cookie.getValue());
                }
            }
        }
        ResponseCookie invalidateCookie = ResponseCookie.from("token", "none").maxAge(0).path("/").domain(".inforum.me").sameSite("none").httpOnly(true).secure(true).build();
        response.addHeader("Set-Cookie", invalidateCookie.toString());
    }

    @PostMapping("/profile/{id}")
    public IsProfileSavedDto createProfileImage(@PathVariable("id") String userId,
                                                @RequestPart(value = "isEmptyProfileImage", required = true) String isEmptyProfileImage,
                                                @RequestPart(value = "image", required = false) Optional<MultipartFile> image,
                                              @RequestPart(value = "introduce", required = false)String introduce,
                                              @RequestPart(value = "tags", required = false) String tags){
        if (isEmptyProfileImage.equals("true") && image.isPresent() || isEmptyProfileImage.equals("false") && image.isEmpty()){
            throw new BadRequestException("image 첨부 유무와 첨부 유무를 나타내는 키-밸류값이 모순");
        }
        MemberInfoDto memberInfoDto;
        try {
            memberInfoDto = MemberInfoDto.createMemberInfo(Boolean.valueOf(isEmptyProfileImage), image.get(), introduce, tags);
        }catch(NoSuchElementException e){
            memberInfoDto = MemberInfoDto.createMemberInfo(Boolean.valueOf(isEmptyProfileImage), null, introduce, tags);
        }
        return memberService.createMemberProfile(userId,memberInfoDto);
    }
    @GetMapping("/profile/{id}")
    public MemberProfileEntity getProfile(@PathVariable("id") String userId){
        try {
            return memberService.getMemberProfile(userId);
        }catch (EmptyResultDataAccessException e){
            throw new NotFoundException("요청 ID:"+userId+" 정보를 찾을 수 없습니다.");
        }
    }

    @PatchMapping("/profile/{id}")
    public IsProfileSavedDto updateProfileImage(@PathVariable("id") String userId,
                                                @RequestPart(value = "isEmptyProfileImage", required = true) String isEmptyProfileImage,
                                                @RequestPart(value = "image", required = false) Optional<MultipartFile> image,
                                                @RequestPart(value = "introduce", required = false)String introduce,
                                                @RequestPart(value = "tags", required = false) String tags){
        MemberInfoDto memberInfoDto;
        try {
            memberInfoDto = MemberInfoDto.createMemberInfo(Boolean.valueOf(isEmptyProfileImage), image.get(), introduce, tags);
        }catch(NoSuchElementException e){
            memberInfoDto = MemberInfoDto.createMemberInfo(Boolean.valueOf(isEmptyProfileImage), null, introduce, tags);
        }
        return memberService.updateMemberProfile(userId,memberInfoDto);
    }

}