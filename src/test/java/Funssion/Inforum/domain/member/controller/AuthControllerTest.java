package Funssion.Inforum.domain.member.controller;

import Funssion.Inforum.domain.member.dto.request.NonSocialMemberLoginDto;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.member.dto.response.TokenDto;
import Funssion.Inforum.domain.member.service.AuthService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup(){
        ReflectionTestUtils.setField(authController,"domain","test-domain.com");
    }
    @Test
    @DisplayName("NonSocial 회원 로그인 성공후 cookie 값 확인")
    public void nonSocalMemberLoginSucceed(){
        //given
        NonSocialMemberLoginDto nonSocialMemberLoginDto = new NonSocialMemberLoginDto("test@gmail.com","a1234567!");
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setServerName("server-example");
        TokenDto expectedTokenDto = new TokenDto("token-value-example","refreshtoken-value-example",1L);
        when(authService.makeTokenInfo(nonSocialMemberLoginDto)).thenReturn(expectedTokenDto);

        //when
        ResponseEntity<IsSuccessResponseDto> responseEntity = authController.nonSocialLogin(nonSocialMemberLoginDto, mockRequest);

        //then
        Assertions.assertThat(responseEntity.getStatusCode().equals(HttpStatus.CREATED));
        Assertions.assertThat(responseEntity.getBody().equals(expectedTokenDto));
        HttpHeaders httpHeaders = responseEntity.getHeaders();
        String cookieHeader = httpHeaders.getFirst(HttpHeaders.SET_COOKIE);
        String tokenValue = cookieHeader.substring(12).split(";")[0];
        Assertions.assertThat(tokenValue).isEqualTo(expectedTokenDto.getAccessToken());
    }






    /*
        순수 service로 테스트 코드를 짜기에는 필요한 인자가 너무 많다 -> mocking을 하자.
     */

    /*   <로그인>
     * 1. 로그인할때 정해진 형식이 주어져 있는데 / 알맞은 로그인 타입이 아닐 경우(email, password 유효성) / 오류발생
     * 2. 로그인할때 형식은 다맞았지만, DB에 정해진 회원정보가 없는데 / 잘못된 회원정보로 로그인한 경우 / 허가 x
     * 3. DB에 정헤진 회원정보가 있고 / 해당 회원정보로 정확히 로그인한 경우 / JWT 토큰이 담긴 쿠키를 발행
     */
//    @Autowired
//    private MockMvc mock;
//
//    @Test
//    @WithMockUser
//    public void getMessageUnauthenticated() {
//        HelloMessageService helloMessageService = new HelloMessageService();
//        assertThrows(AuthenticationCredentialsNotFoundException.class,()->{
//            helloMessageService.getMessage();
//        });
//    }
//    class HelloMessageService{
//        @PreAuthorize("authenticated")
//        public String getMessage() {
//            Authentication authentication = SecurityContextHolder.getContext()
//                    .getAuthentication();
//            return "Hello " + authentication;
//        }
//    }
}
