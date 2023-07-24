package Funssion.Inforum.domain.member.controller;

import Funssion.Inforum.domain.member.dto.NonSocialMemberLoginForm;
import Funssion.Inforum.domain.member.dto.TokenDto;
import Funssion.Inforum.domain.member.swagger.ErrorResponse;
import Funssion.Inforum.domain.member.swagger.SuccessResponse;
import Funssion.Inforum.jwt.JwtFilter;
import Funssion.Inforum.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;



    @Operation(summary = "로그인 API",description = "소셜로그인/일반로그인 구분 필수", tags = {"Member"})
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공, redirection 필요", content = @Content(schema = @Schema(implementation = SuccessResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 사용자 정보입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "503", description = "해당 요청은 아직 구현되지 않았습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody NonSocialMemberLoginForm nonSocialMemberLoginForm) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(nonSocialMemberLoginForm.getUser_email(), nonSocialMemberLoginForm.getUser_pw());
        log.info("authetntication manager builder get object = {}",authenticationManagerBuilder.getObject());
        // authenticate 메소드가 실행이 될 때 CustomUserDetailsService class의 loadUserByUsername 메소드가 실행 및 db와 대조하여 인증
        log.info("credentials on token = {}",authenticationToken.getCredentials());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("authentication info = {}",authentication);
        // 해당 객체를 SecurityContextHolder에 저장하고
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 인증받은 새로운 authentication 객체를 createToken 메소드를 통해서 JWT Token을 생성
        String jwt = tokenProvider.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        // response header에 jwt token에 넣어줌
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        // tokenDto를 이용해 response body에도 넣어서 리턴
        return new ResponseEntity<>(new TokenDto(jwt,Long.parseLong(authentication.getName())), httpHeaders, HttpStatus.OK);
    }
}