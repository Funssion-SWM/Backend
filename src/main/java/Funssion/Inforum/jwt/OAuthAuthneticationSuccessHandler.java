package Funssion.Inforum.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;



@Component
@RequiredArgsConstructor
@Slf4j
public class OAuthAuthneticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    @Value("${jwt.domain}") private String domain;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);
        String accessToken = tokenProvider.createToken(authentication);
        log.info("token 정보 = {}",accessToken);

        if(request.getServerName().equals("localhost")){
            String cookieValue = "accessToken=" + accessToken + "; Path=/; Domain=" + domain + "; Max-Age=1800; HttpOnly";
            response.setHeader("Set-Cookie", cookieValue);
        }
        else{
            String cookieValue = "accessToken="+accessToken+"; "+"Path=/; "+"Domain="+domain+"; "+"Max-Age=1800; HttpOnly; SameSite=None; Secure";
            response.setHeader("Set-Cookie",cookieValue);
        }
        String redirectUrl = UriComponentsBuilder.fromUriString("/api/users/oauth2/login").build().toString();
        response.sendRedirect(redirectUrl);


    }
}
