package Funssion.Inforum.domain.member.controller;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.member.dto.request.EmailRequestDto;
import Funssion.Inforum.domain.member.dto.response.EmailDto;
import Funssion.Inforum.domain.member.dto.response.ValidatedDto;
import Funssion.Inforum.domain.member.service.MailService;
import Funssion.Inforum.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    MemberService memberService;
    @MockBean
    MailService mailService;
    @Test
    @WithMockUser
    @DisplayName("닉네임으로 가입한 이메일 정보 가져오기")
    void getEmailByNickname() throws Exception {
        //given
        String nickname = "test_nickname";
        String returnEmail = "tes**@gmail.com";
        when(memberService.findEmailByNickname(nickname)).
                thenReturn(new EmailDto(returnEmail,"해당 닉네임으로 등록된 이메일 정보입니다."));
        //when
        MvcResult result = mvc.
                perform(get("/users/find-email-by")
                        .param("nickname", nickname))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        String email = JsonPath.read(responseBody, "$.email");

        //then
        assertThat(email).isEqualTo(returnEmail);
    }

    @Test
    @WithMockUser
    @DisplayName("비밀번호 수정을 위해 비밀번호 수정 페이지 링크 담긴 이메일 전송하기")
    void sendEmailCodeForPassword() throws Exception {
        EmailRequestDto validEmailRequestDto = new EmailRequestDto("test_valid@gmail.com");
        EmailRequestDto inValidEmailRequestDto = new EmailRequestDto("test_invalid@gmail.com");
        String validEmailRequest = objectMapper.writeValueAsString(validEmailRequestDto);
        String inValidEmailRequest = objectMapper.writeValueAsString(inValidEmailRequestDto);
        when(memberService.isRegisteredEmail(validEmailRequestDto.getEmail())).thenReturn(new ValidatedDto(true,"유효함"));
        when(memberService.isRegisteredEmail(inValidEmailRequestDto.getEmail())).thenReturn(new ValidatedDto(false,"유효하지 않음"));
        when(mailService.sendEmailLink(validEmailRequestDto.getEmail())).thenReturn(new IsSuccessResponseDto(true,"성공적으로 해당 이메일로 비밀번호 수정 링크를 전송하였습니다!"));

        MvcResult validResult = mvc.perform(post("/users/authenticate-email/find")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(validEmailRequest))
                .andExpect(status().isOk())
                .andReturn();
        String successResponseBody = validResult.getResponse().getContentAsString();
        Boolean success = JsonPath.read(successResponseBody, "$.isSuccess");

        MvcResult inValidResult = mvc.perform(post("/users/authenticate-email/find")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(inValidEmailRequest))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = inValidResult.getResponse().getContentAsString();
        Boolean fail = JsonPath.read(responseBody, "$.isSuccess");

        assertThat(success).isEqualTo(true);
        assertThat(fail).isEqualTo(false);
    }



    /*
     * <회원가입>
     * 1. 회원가입시 알맞은 형식이 있는데 / 지켜지지 않았을 경우 / 오류발생
     * 2. 회원가입시 형식도 다 맞았고 (중복된 회원정보가 아니라고 가정하고, 단위테스트이니까) / 지켜졌을 경우 / 저장된 회원정보의 PK값 반환

     * <이메일 유효성 체크>
     * 1. 유효한 이메일이 파라미터로 왔는지 체크해야하는 검증이 필요할듯 (추가)
     * 2. 요청한 이메일이 이미 DB에 있다고 가정하고 / 같은 이메일이 올경우 / 오류발생
     * 3. 요청한 이메일이 DB에 없으면 / 이메일이 올경우 / ValidatedDto 유효성 반환

     * <이메일 인증코드 전송>
     * 1. 이메일 인증코드가 정상적으로 작동한다고 가정하고 / 검증할 이메일이 요청되었을 때 / 성공적인 이메일 전송 객체 반환
     * 2. 이메일 인증코드 정상작동 안할 경우/ 검증한 이메일이 요청되었을 때 /실패 객체 반환

     * <이메일 인증코드 확인> (어케...?)
     * 1. 인증 코드가 정상적으로 이메일에 전송되었다고 가정 / 해당 코드와 사용자가 요청한 코드가 같을 때 / 성공
     * 2. 인증 코드가 비정상적으로 작동 / 사용자 요청과 상관없이 / 실패

     * <닉네임 유효성 체크>
     * 1. 유효한 닉네임이 파라미터로 왔는지 체크해야하는 검증이 필요할듯 (추가)
     * 2. 요청한 닉네임이 이미 DB에 있다고 가정하고 / 같은 닉네임이 올경우 / 오류발생
     * 3. 요청한 닉네임이 DB에 없으면 / 닉네임이 올경우 / ValidatedDto 유효성 반환

     * <사용자 확인 API>
     * 1. Request에 쿠키값이 존재하고, 해당 쿠키값에 유효한 토큰이 있다고 가정하면 / API 호출시 / MemberID를 담은 DTO반환
     * 2. 쿠키값이 없으면 / API호출시 / -1의 ID를 담은 DTO반환
     * 3. 쿠키값이 존재해도, 해당 쿠키의 토큰값이 유효하지 않을경우 어떻게 파악..?

     * <로그아웃>
     * 1. 요청에 token= 이 담긴 쿠키가 들어왔다고 가정/ 요청받은 쿠키값들을 모두 뒤져서, token값을 뽑아냈을 때 / response cookie의 age를 0으로 설정하여 만료시킴
     * 2. 요청에 token= 이 담긴 쿠키가 들어오지 않았다고 가정/ 요청에 해당 쿠키가 없어도 / 만료

     * */
}