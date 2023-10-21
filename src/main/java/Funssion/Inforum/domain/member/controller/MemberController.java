package Funssion.Inforum.domain.member.controller;


import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.member.dto.request.*;
import Funssion.Inforum.domain.member.dto.response.*;
import Funssion.Inforum.domain.member.service.MailService;
import Funssion.Inforum.domain.member.service.MemberService;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;


    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public SaveMemberResponseDto create(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid MemberSaveDto memberSaveDto) throws IOException { //dto로 바꿔야함
        return memberService.requestMemberRegistration(memberSaveDto,request,response);
    }

    @PostMapping("/authenticate-email")
    public IsSuccessResponseDto mailSend(@RequestBody @Valid EmailRequestDto emailDto) {
        String decodedEmail = URLDecoder.decode(emailDto.getEmail(), StandardCharsets.UTF_8);
        if (memberService.isValidEmail(decodedEmail).isValid()) {
            return mailService.sendEmailCode(emailDto.getEmail());
        } else {
            return new IsSuccessResponseDto(false, "이미 등록된 이메일입니다.");
        }
    }
    @PostMapping("/authenticate-email/find")
    public IsSuccessResponseDto mailSendToFindPassword(@RequestBody @Valid EmailRequestDto emailDto){
        ValidatedDto canFindEmail = memberService.isRegisteredEmail(emailDto.getEmail());
        if (canFindEmail.isValid()) {
            mailService.sendEmailLink(emailDto.getEmail());
            return new IsSuccessResponseDto(true, "해당 이메일로 인증번호 링크를 전송하였습니다.");
        } else {
            return new IsSuccessResponseDto(false, canFindEmail.getMessage());
        }
    }
    @PutMapping("/password")
    public IsSuccessResponseDto updatePassword(@RequestBody @Valid PasswordUpdateDto passwordUpdateDto ){
        return memberService.findAndChangePassword(passwordUpdateDto);
    }

    @PostMapping("/authenticate-code")
    public ValidatedDto AuthCheck(@RequestBody @Valid CodeCheckDto codeCheckDto) {
        return mailService.isAuthorizedEmail(codeCheckDto);
    }

    @GetMapping("/check-duplication")
    public ValidatedDto isValidName(@RequestParam(value = "name", required = true) String name) {
        return memberService.isValidName(name);
    }
    @PostMapping("/nickname/{id}")
    public IsSuccessResponseDto registerName(@PathVariable("id") String userId,@RequestBody NicknameRequestDto nicknameRequestDto){
        return memberService.requestNicknameRegistration(nicknameRequestDto,Long.valueOf(userId));
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
        AuthUtils.logout(request, response);
    }

    @PostMapping("/withdraw")
    public void withdraw(HttpServletRequest request, HttpServletResponse response) {
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        memberService.withdrawUser(userId);
        AuthUtils.logout(request, response);
    }

    @PostMapping("/profile/{id}")
    public IsProfileSavedDto createProfileImage(@PathVariable("id") Long userId,
                                                @RequestPart(value = "isEmptyProfileImage", required = true) String isEmptyProfileImage,
                                                @RequestPart(value = "image", required = false) Optional<MultipartFile> image,
                                              @RequestPart(value = "introduce", required = false)String introduce,
                                              @RequestPart(value = "tags", required = true) String tags){
        List<String> tagList = exceptionHandleOfList(tags);
        if (isEmptyProfileImage.equals("true") && image.isPresent() || isEmptyProfileImage.equals("false") && image.isEmpty()){
            throw new BadRequestException("image 첨부 유무와 첨부 유무를 나타내는 키-밸류값이 모순");
        }
        MemberInfoDto memberInfoDto;
        try {
            memberInfoDto = MemberInfoDto.createMemberInfo(Boolean.valueOf(isEmptyProfileImage), image.get(), introduce, tagList);
        }catch(NoSuchElementException e){
            memberInfoDto = MemberInfoDto.createMemberInfo(Boolean.valueOf(isEmptyProfileImage), null, introduce, tagList);
        }
        return memberService.createMemberProfile(userId,memberInfoDto);
    }

    private List<String> exceptionHandleOfList(String tags) {
        List<String> tagList = new ArrayList<>();
        if (!tags.equals("[]")) tagList = convertStringToList(tags);
        return tagList;
    }

    private List<String> convertStringToList(String tags) {
        /**
         * 클라이언트에서 모든 도메인의 같은 tag 요청을 보낼 수 있게 "~~" 의 따옴표도 삭제합니다.
         * form-data 형식이라 어쩔 수 없음.
         */
        return List.of(tags.substring(1, tags.length() - 1).split(","))
                .stream().map(tag->tag.substring(1,tag.length()-1))
                .collect(Collectors.toList());
    }

    @GetMapping("/profile/{id}")
    public MemberProfileDto getProfile(@PathVariable("id") Long userId){
        try {
            return memberService.getMemberProfile(userId);
        }catch (EmptyResultDataAccessException e){
            throw new NotFoundException("요청 ID:"+userId+" 정보를 찾을 수 없습니다.");
        }
    }

    @PatchMapping("/profile/{id}")
    public IsProfileSavedDto updateProfileImage(@PathVariable("id") Long userId,
                                                @RequestPart(value = "isEmptyProfileImage", required = true) String isEmptyProfileImage,
                                                @RequestPart(value = "image", required = false) Optional<MultipartFile> image,
                                                @RequestPart(value = "introduce", required = false)String introduce,
                                                @RequestPart(value = "tags", required = false) String tags){

        if(isNotOwnerOfProfile(userId)) throw new UnAuthorizedException("해당 유저의 프로필 수정 권한이 없습니다.");

        List<String> tagList = exceptionHandleOfList(tags);
        MemberInfoDto memberInfoDto;
        try {
            memberInfoDto = MemberInfoDto.createMemberInfo(Boolean.valueOf(isEmptyProfileImage), image.get(), introduce, tagList);
        }catch(NoSuchElementException e){
            memberInfoDto = MemberInfoDto.createMemberInfo(Boolean.valueOf(isEmptyProfileImage), null, introduce, tagList);
        }
        return memberService.updateMemberProfile(userId,memberInfoDto);
    }

    private boolean isNotOwnerOfProfile(Long userId) {
        return !userId.equals(SecurityContextUtils.getAuthorizedUserId());
    }


    @GetMapping("/find-email-by")
    public EmailDto findEmailByNickname(@RequestParam String nickname){
        return memberService.findEmailByNickname(nickname);
    }
}