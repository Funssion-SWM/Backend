package Funssion.Inforum.domain.member.controller;

import Funssion.Inforum.domain.member.dto.NonSocialMemberLoginDto;
import Funssion.Inforum.domain.member.dto.TokenDto;
import Funssion.Inforum.domain.member.service.AuthService;
import Funssion.Inforum.domain.member.swagger.ErrorResponse;
import Funssion.Inforum.domain.member.swagger.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {
    private final AuthService authService;
    @Value("${jwt.domain}") String domain;

    @Operation(summary = "로그인 API",description = "소셜로그인/일반로그인 구분 필수", tags = {"Member"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공, redirection 필요", content = @Content(schema = @Schema(implementation = SuccessResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 사용자 정보입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "503", description = "해당 요청은 아직 구현되지 않았습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody NonSocialMemberLoginDto nonSocialMemberLoginDto, HttpServletRequest request) {
        TokenDto tokenDto = authService.makeTokenInfo(nonSocialMemberLoginDto);
        HttpHeaders httpHeaders = new HttpHeaders();

//        if (request.isSecure()) {
//            httpHeaders.add("Set-Cookie", "token="+tokenDto.getToken()+"; "+"Path=/; "+"Domain="+domain+"; "+"Max-Age=1800; HttpOnly; SameSite=None; Secure");
//        } else {
//            //SSL 미설정으로 인한 Secure 옵션 미설정
//            httpHeaders.add("Set-Cookie", "token="+tokenDto.getToken()+"; "+"Path=/; "+"Domain="+domain+"; "+"Max-Age=1800; HttpOnly; SameSite=None; Secure");
//        }
        if(request.getServerName().equals("localhost")){
            httpHeaders.add("Set-Cookie", "token="+tokenDto.getToken()+"; "+"Path=/; "+"Domain="+domain+"; "+"Max-Age=1800; HttpOnly");
        }
        else{
            httpHeaders.add("Set-Cookie", "token="+tokenDto.getToken()+"; "+"Path=/; "+"Domain="+domain+"; "+"Max-Age=1800; HttpOnly; SameSite=None; Secure");
        }

        return new ResponseEntity<>(tokenDto, httpHeaders, HttpStatus.OK);
    }
}