package Funssion.Inforum.domain.member.controller;

import Funssion.Inforum.domain.member.dto.MemberSaveForm;
import Funssion.Inforum.domain.member.service.MemberService;
import Funssion.Inforum.swagger.ErrorResponse;
import Funssion.Inforum.swagger.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공, redirection 필요", content = @Content(schema=@Schema(implementation= SuccessResponse.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 회원 정보입니다.", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
            @ApiResponse(responseCode = "503", description = "해당 요청은 아직 구현되지 않았습니다.", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
    })
    public ResponseEntity create(@RequestBody @Validated MemberSaveForm memberSaveForm, BindingResult bindingResult) throws NoSuchAlgorithmException { //dto로 바꿔야함
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        log.info("create in controller, member = {}", memberSaveForm);
//        Long save_id = memberService.join(nonSocialMemberSaveForm);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ResponseBody
    public String validateDuplicateEmail(@RequestParam(value="email", required=true) String email){
        MemberSaveForm memberSaveForm = new MemberSaveForm();
        memberSaveForm.setUser_email(email);
//        memberService.validateDuplicateEmail(nonSocialMemberSaveForm, nonSocialMemberSaveForm.getLogin_type());
        return "ok";
    }
    @ResponseBody
    @GetMapping("/check")
    public String checkToken(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}