//package Funssion.Inforum.swagger.member.controller;
//
//import Funssion.Inforum.swagger.member.form.LoginForm;
//import Funssion.Inforum.swagger.member.form.MemberSaveForm;
//import Funssion.Inforum.swagger.ErrorResponse;
//import Funssion.Inforum.swagger.member.response.LoginValidResponse;
//import Funssion.Inforum.swagger.member.response.RegisterValidResponse;
//import Funssion.Inforum.swagger.SuccessResponse;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//
//
//@RequiredArgsConstructor
//@Tag(name = "Member", description = "사용자 정보 API")
//public class SwaggerMemberController {
//    @Operation(summary = "닉네임 중복체크",description = "같은 닉네임을 가진 유저는 없어야 하므로, 중복체크 필수", tags = {"Member"})
//    @GetMapping("/auth/nickname/{MyNickname}")
//    @ResponseBody
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "닉네임 사용 가능", content = @Content(schema=@Schema(implementation= SuccessResponse.class),mediaType = "application/json")),
//            @ApiResponse(responseCode = "409", description = "닉네임 중복, 사용불가", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
//            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
//    })
//    public String checkDuplicateNickname(@Parameter(description="중복체크할 닉네임을 파라미터로 설정", required = true) String MyNickname){
//        return "OK";
//    }
//    @Operation(summary = "이메일 중복체크",description = "같은 이메일을 가진 유저는 없어야 하므로, 중복체크 필수", tags = {"Member"})
//    @GetMapping("/auth/email/{MyEmail}")
//    @ResponseBody
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "이메일 사용 가능", content = @Content(schema=@Schema(implementation= SuccessResponse.class),mediaType = "application/json")),
//            @ApiResponse(responseCode = "409", description = "이메일 중복, 사용불가", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
//            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
//    })
//    public String checkDuplicateEmail(@Parameter(description="중복체크할 이메일을 파라미터로 설정", required = true) String MyEmail){
//        return "OK";
//    }
//    @Operation(summary = "이메일 확인 위한 인증코드 발급",description = "중복확인 완료한 이메일로 인증번호 전송", tags = {"Member"})
//    @PostMapping("auth/email_code")
//    @ResponseBody
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "인증코드 발송 성공", content = @Content(schema=@Schema(implementation= SuccessResponse.class),mediaType = "application/json")),
//            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
//    })
//    public String sendEmailCode(@RequestBody Email email){
//        return "ok";
//    }
//
//    @Operation(summary = "이메일로 전송된 인증코드 확인",description = "이메일로 전송된 인증번호 맞는지 확인", tags = {"Member"})
//    @PostMapping("auth/email_code/validity")
//    @ResponseBody
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "이메일 인증 성공", content = @Content(schema=@Schema(implementation= SuccessResponse.class),mediaType = "application/json")),
//            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
//    })
//    public String validateDuplicateEmail(@RequestBody EmailCode emailCode){
//        return "ok";
//    }
//    @Operation(summary = "회원가입 실행 API",description = "소셜로그인/일반로그인 구분 필수", tags = {"Member"})
//    @PostMapping("/users")
//    @ResponseBody
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "302", description = "회원가입 성공, redirection", content = @Content(schema=@Schema(implementation= RegisterValidResponse.class),mediaType = "application/json")),
//            @ApiResponse(responseCode = "409", description = "이미 존재하는 회원 정보입니다.", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
//            @ApiResponse(responseCode = "503", description = "해당 요청은 아직 구현되지 않았습니다.", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
//    })
//    public ResponseEntity<RegisterValidResponse> create(@RequestBody @Validated MemberSaveForm memberSaveForm, BindingResult bindingResult){ //dto로 바꿔야함
//        RegisterValidResponse registerValidResponse = new RegisterValidResponse(1);
//        return ResponseEntity.ok().body(registerValidResponse);
//    }
//
//    @Operation(summary = "로그인 API",description = "소셜로그인/일반로그인 구분 필수", tags = {"Member"})
//    @PostMapping("/users/login")
//    @ResponseBody
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "302", description = "로그인 성공, redirection", content = @Content(schema=@Schema(implementation= LoginValidResponse.class),mediaType = "application/json")),
//            @ApiResponse(responseCode = "400", description = "아이디 혹은 비밀번호가 맞지 않음", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
//            @ApiResponse(responseCode = "503", description = "해당 요청은 아직 구현되지 않았습니다.", content = @Content(schema=@Schema(implementation= ErrorResponse.class),mediaType = "application/json")),
//    })
//    public ResponseEntity<LoginValidResponse> login(@RequestBody @Validated LoginForm loginForm, BindingResult bindingResult){ //dto로 바꿔야함
//        LoginValidResponse loginValidResponse = new LoginValidResponse(1,"access_token_val","refresh_token_val");
//        return ResponseEntity.ok().body(loginValidResponse);
//    }
//
//
//
//    @Data
//    public class Email{
//        @Schema(description="이메일",example="abcde@gmail.com")
//        private String email;
//    }
//
//    @Data
//    public class EmailCode{
//        @Schema(description="이메일 인증위한 코드",example="12ABC3")
//        private String code;
//    }
//
//}