package Funssion.Inforum.jwt;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Collection;


@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    @Value("${jwt.domain}") private String domain;
    @Value("${oauth-signup-uri}") private String signUpURI;
    @Value("${oauth-signin-uri}") private String signInURI;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        /**
         * 일반 로그인일 경우 생성되는 Authentication 객체를 상속한 UsernamePasswordAuthenticationToken 으로 response 생성
         * 바로 jwt 토큰 발급하여 response 에 쿠키를 추가합니다.
         */
        if (authentication instanceof UsernamePasswordAuthenticationToken){
            makeSuccessResponseBody(response);
            resolveResponseCookieByOrigin(request, response, accessToken, refreshToken);
            return;
        }

        resolveResponseCookieByOrigin(request, response, accessToken, refreshToken);
        response.sendRedirect(redirectUriByFirstJoinOrNot(authentication));

    }

    private static void makeSuccessResponseBody(HttpServletResponse response) throws IOException {
        String successResponse = convertSuccessObjectToString();
        response.setStatus(response.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(successResponse);
    }

    private static String convertSuccessObjectToString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        IsSuccessResponseDto isSuccessResponseDto = new IsSuccessResponseDto(true, "로그인에 성공하였습니다.");
        String successResponse = objectMapper.writeValueAsString(isSuccessResponseDto);
        return successResponse;
    }

    private void resolveResponseCookieByOrigin(HttpServletRequest request, HttpServletResponse response, String accessToken, String refreshToken){
        if(request.getServerName().equals("localhost") || request.getServerName().equals("dev.inforum.me")){
            addCookie(accessToken, refreshToken, response,false);
        }
        else{
            addCookie(accessToken, refreshToken, response,true);
        }
    }

    private void addCookie(String accessToken, String refreshToken, HttpServletResponse response,boolean isHttpOnly) {
        String accessCookieString = makeAccessCookieString(accessToken, isHttpOnly);
        String refreshCookieString = makeRefreshCookieString(refreshToken, isHttpOnly);
        response.setHeader("Set-Cookie", accessCookieString);
        response.addHeader("Set-Cookie", refreshCookieString);
    }

    private String makeAccessCookieString(String token,boolean isHttpOnly) {
        if(isHttpOnly){
            return "accessToken=" + token + "; Path=/; Domain=" + domain + "; Max-Age=3600; SameSite=Lax; HttpOnly; Secure";
        }else{
            return "accessToken=" + token + "; Path=/; Domain=" + domain + "; Max-Age=3600; SameSite=Lax; Secure";
        }
    }

    private String makeRefreshCookieString(String token,boolean isHttpOnly) {
        if(isHttpOnly){
            return "refreshToken=" + token + "; Path=/; Domain=" + domain + "; Max-Age=864000; SameSite=Lax; HttpOnly; Secure";
        }else{
            return "refreshToken=" + token + "; Path=/; Domain=" + domain + "; Max-Age=864000; SameSite=Lax; Secure";
        }
    }

    private String redirectUriByFirstJoinOrNot(Authentication authentication){
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
        //사실 authority 가 ROLE_FIRST_JOIN인게 이상하긴함. 하지만 authentication 객체를 활용하기 위해서 해당 방법을 사용하였음.
        //어차피 role은 우리 로직엔 사용되지 않기 때문임.
        if(authorities.stream().filter(o -> o.getAuthority().equals("ROLE_FIRST_JOIN")).findAny().isPresent()){
            return UriComponentsBuilder.fromHttpUrl(signUpURI)
                    .path(authentication.getName())
                    .build().toString();

        }
        else{ // non social 로그인의 경우 회원가입한 유저이므로 else문으로 항상 들어감.
            return UriComponentsBuilder.fromHttpUrl(signInURI)
                    .build().toString();
        }
    }
}
