package Funssion.Inforum.jwt;

import io.jsonwebtoken.ExpiredJwtException;
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
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final String domain;

    // 실제 필터링 로직
    // 토큰의 인증정보를 SecurityContext에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveAccessToken(request);
        String requestURI = request.getRequestURI();
        log.info("jwt token in cookie check = {}, requestURI ={}", accessToken,requestURI);
        try {
            if (StringUtils.hasText(accessToken) && tokenProvider.validateAccessToken(accessToken)) {
                Authentication authentication = tokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);

            }
            else {
                log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
                makeRTRLogin(request, response, requestURI);
            }
        }catch(ExpiredJwtException e){
            log.info("jwt 토큰 refresh로 갱신");
            makeRTRLogin(request, response, requestURI);
        }

        filterChain.doFilter(request, response);
    }

    private void makeRTRLogin(HttpServletRequest request, HttpServletResponse response, String requestURI) throws IOException {
        String refreshToken = resolveRefreshToken(request);
        try {
            if (StringUtils.hasText(refreshToken) && tokenProvider.validateRefreshToken(refreshToken)){
                Authentication authentication = makeNewAccessAndRefreshTokenForRTR(request, response, refreshToken);
                log.debug("RTR방식 사용, access token, refresh 토큰 갱신 및, 사용자 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
            }
            else{
                log.debug("유효한 Refresh JWT 토큰이 없습니다, uri: {}", requestURI);
            }
        }catch(ExpiredJwtException error){
            log.info("RTR 로그인방식 적용 안됨");
            response.sendRedirect("https://www.inforum.me/login");
        }
    }

    private Authentication makeNewAccessAndRefreshTokenForRTR(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String newAccessToken = tokenProvider.createAccessToken(authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(authentication);
        if(request.getServerName().equals("localhost")){
            String cookieValue1 = "accessToken=" + newAccessToken + "; Path=/; Domain=" + domain + "; Max-Age=1800; SameSite=Lax; HttpOnly";
            String cookieValue2 = "refreshToken=" + newRefreshToken + "; Path=/; Domain=" + domain + "; Max-Age=86400; SameSite=Lax; HttpOnly";
            response.addHeader("Set-Cookie", cookieValue1);
            response.addHeader("Set-Cookie", cookieValue2);
            log.info("domain = {}",domain);

        }
        else{
            String cookieValue1 = "accessToken="+newAccessToken+"; "+"Path=/; "+"Domain="+domain+"; "+"Max-Age=1800; HttpOnly; SameSite=Lax; Secure";
            String cookieValue2 = "refreshToken=" + newRefreshToken + "; Path=/; Domain=" + domain + "; Max-Age=86400; HttpOnly; SameSite=Lax; Secure";
            response.addHeader("Set-Cookie",cookieValue1);
            response.addHeader("Set-Cookie", cookieValue2);
        }
        return authentication;
    }

    // Request Header 에서 토큰 정보를 꺼내오기 위한 메소드
    private String resolveAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    log.info("access token value = {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    log.info("refresh token value = {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return "";
    }
}
