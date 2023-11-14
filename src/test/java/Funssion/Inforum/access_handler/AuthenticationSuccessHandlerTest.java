package Funssion.Inforum.access_handler;

import Funssion.Inforum.common.constant.Role;
import Funssion.Inforum.domain.member.entity.CustomUserDetails;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthenticationSuccessHandlerTest {

    @Value("${oauth-signup-uri}") String signUpURI;
    @Value("${oauth-signin-uri}") String signInURI;

    @Autowired
    AuthenticationSuccessHandler authenticationSuccessHandler;
    @Test
    @DisplayName("OAuth 유저 최초로그인시 redirect uri 확인")
    void firstLoginThenRedirectToNicknamePage() throws IOException {
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(new CustomUserDetails(String.valueOf(1L), Role.addRole(Role.getIncludingRoles(Role.USER.toString()), Role.OAUTH_FIRST_JOIN), Map.of()));
        when(mockAuthentication.getName()).thenReturn("name");
        Assertions.assertThat(authenticationSuccessHandler.getRedirectUri(mockAuthentication)).isEqualTo(signUpURI+"name");
    }
    @Test
    @DisplayName("OAuth 유저 로그인시 redirect uri 확인")
    void LoginThenRedirectToMainPage() throws IOException {
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(new CustomUserDetails(String.valueOf(1L), Role.USER.toString(), Map.of()));
        when(mockAuthentication.getName()).thenReturn("name");
        Assertions.assertThat(authenticationSuccessHandler.getRedirectUri(mockAuthentication)).isEqualTo(signInURI);
    }

}