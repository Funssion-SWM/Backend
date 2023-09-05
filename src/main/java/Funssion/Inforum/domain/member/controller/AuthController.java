package Funssion.Inforum.domain.member.controller;

import Funssion.Inforum.domain.member.dto.request.NonSocialMemberLoginDto;
import Funssion.Inforum.domain.member.dto.response.IsSuccessResponseDto;
import Funssion.Inforum.domain.member.dto.response.TokenDto;
import Funssion.Inforum.domain.member.dto.response.isAlreadyExistSocialMember;
import Funssion.Inforum.domain.member.service.AuthService;
import Funssion.Inforum.domain.member.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OAuthService oAuthService;
    @Value("${jwt.domain}") private String domain;

    @PostMapping("/users/login")
    public ResponseEntity<IsSuccessResponseDto> nonSocialLogin(@Valid @RequestBody NonSocialMemberLoginDto nonSocialMemberLoginDto, HttpServletRequest request) {
        try {
            TokenDto tokenDto = authService.makeTokenInfo(nonSocialMemberLoginDto);
            HttpHeaders httpHeaders = new HttpHeaders();
            if(request.getServerName().equals("localhost")){
                httpHeaders.add("Set-Cookie", "accessToken="+tokenDto.getToken()+"; "+"Path=/; "+"Domain="+domain+"; "+"Max-Age=86400;");
            }
            else{
                httpHeaders.add("Set-Cookie", "accessToken="+tokenDto.getToken()+"; "+"Path=/; "+"Domain="+domain+"; "+"Max-Age=86400; SameSite=None; Secure");
            }
            return new ResponseEntity<>( new IsSuccessResponseDto(true,"로그인에 성공하였습니다."), httpHeaders, HttpStatus.OK);
        }catch(AuthenticationException e){
            return new ResponseEntity<>(new IsSuccessResponseDto(false,"로그인에 실패하였습니다."),HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/users/oauth2/login")
    public isAlreadyExistSocialMember socialLogin(@RequestParam("isSignUp") Boolean isSignUp, Authentication authentication){
        return new isAlreadyExistSocialMember(isSignUp,Long.valueOf(authentication.getName()));
    }
}