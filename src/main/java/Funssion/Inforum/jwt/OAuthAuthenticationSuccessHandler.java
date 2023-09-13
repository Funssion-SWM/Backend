package Funssion.Inforum.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class OAuthAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    @Value("${jwt.domain}") private String domain;
    @Value("${oauth-signup-uri}") private String signUpURI;
    @Value("${oauth-signin-uri}") private String signInURI;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);
        boolean isHttpOnly;
        if(request.getServerName().equals("localhost")){
            isHttpOnly = false;
            String accessCookieString = makeAccessCookieString(accessToken,isHttpOnly);
            String refreshCookieString = makeRefreshCookieString(refreshToken,isHttpOnly);
            response.setHeader("Set-Cookie", accessCookieString);
            response.setHeader("Set-Cookie", refreshCookieString);
            response.sendRedirect(redirectUriByFirstJoinOrNot(authentication));
        }
        else{
            isHttpOnly =true;
            String accessCookieString = makeAccessCookieString(accessToken,isHttpOnly);
            String refreshCookieString = makeRefreshCookieString(refreshToken,isHttpOnly);
            response.setHeader("Set-Cookie",accessCookieString);
            response.setHeader("Set-Cookie", refreshCookieString);
            response.sendRedirect(redirectUriByFirstJoinOrNot(authentication));
        }
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
        else{
            return UriComponentsBuilder.fromHttpUrl(signInURI)
                    .build().toString();
        }
    }
}
