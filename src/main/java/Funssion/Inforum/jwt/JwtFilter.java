package Funssion.Inforum.jwt;

import Funssion.Inforum.domain.member.constant.Token;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

import static Funssion.Inforum.domain.member.constant.Token.ACCESS_TOKEN;
import static Funssion.Inforum.domain.member.constant.Token.REFRESH_TOKEN;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final String domain;

    // 실제 필터링 로직
    // 토큰의 인증정보를 SecurityContext에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request, ACCESS_TOKEN);

        if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) {
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        else {
            RTRLogin(request, response);
        }
        filterChain.doFilter(request, response);
    }

    private void RTRLogin(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = resolveToken(request,REFRESH_TOKEN);

        if (StringUtils.hasText(refreshToken) && tokenProvider.validateToken(refreshToken)){
            makeNewAccessAndRefreshTokenForRTR(request, response, refreshToken);
        }
    }

    private void makeNewAccessAndRefreshTokenForRTR(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String newAccessToken = tokenProvider.createAccessToken(authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(authentication);


        if(request.getServerName().equals("localhost")){
            setCookieForLocal(response, newAccessToken, newRefreshToken);
        }
        else{
            setCookieForProd(response, newAccessToken, newRefreshToken);
        }
    }

    private void setCookieForProd(HttpServletResponse response, String newAccessToken, String newRefreshToken) {
        String cookieValue1 = "accessToken="+ newAccessToken +"; "+"Path=/; "+"Domain="+domain+"; "+"Max-Age=1800; HttpOnly; SameSite=Lax; Secure";
        String cookieValue2 = "refreshToken=" + newRefreshToken + "; Path=/; Domain=" + domain + "; Max-Age=864000; HttpOnly; SameSite=Lax; Secure";
        response.addHeader("Set-Cookie",cookieValue1);
        response.addHeader("Set-Cookie", cookieValue2);
    }

    private void setCookieForLocal(HttpServletResponse response, String newAccessToken, String newRefreshToken) {
        String cookieValue1 = "accessToken=" + newAccessToken + "; Path=/; Domain=" + domain + "; Max-Age=1800; SameSite=Lax; HttpOnly";
        String cookieValue2 = "refreshToken=" + newRefreshToken + "; Path=/; Domain=" + domain + "; Max-Age=864000; SameSite=Lax; HttpOnly";
        response.addHeader("Set-Cookie", cookieValue1);
        response.addHeader("Set-Cookie", cookieValue2);
    }

    private String resolveToken(HttpServletRequest request, Token token){
        Cookie[] cookies = request.getCookies();

        if (Objects.isNull(cookies)) return "";

        for (Cookie cookie : cookies) {
            if (token.getType().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return "";
    }

}
