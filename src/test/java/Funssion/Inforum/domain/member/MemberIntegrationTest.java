//package Funssion.Inforum.domain.member;
//
//import Funssion.Inforum.domain.member.constant.LoginType;
//import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jayway.jsonpath.JsonPath;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//public class MemberIntegrationTest {
//    @Autowired
//    MockMvc mvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
////    @Autowired
////    MemberService memberService;
////    @Autowired
////    MemberRepository memberRepository;
//
//    @Test
//    @DisplayName("닉네임으로 가입한 후에, 해당 닉네임으로 이메일을 찾는다")
//    void findEmailByNickname() throws Exception {
//        //given
//        MemberSaveDto registeredUser = MemberSaveDto.builder()
//                .userEmail("test1@gmail.com")
//                .loginType(LoginType.NON_SOCIAL)
//                .userName("test_nickname")
//                .userPw("a1234567!")
//                .build();
//        String objectUserMappingToString = objectMapper.writeValueAsString(registeredUser);
//        mvc.perform(post("/users")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectUserMappingToString))
//                .andExpect(status().isCreated())
//                .andReturn();
//
//        //when
//        MvcResult result = mvc.
//                perform(get("/users/find-email-by")
//                        .param("nickname", registeredUser.getUserName()))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String responseBody = result.getResponse().getContentAsString();
//        String email = JsonPath.read(responseBody, "$.email");
//
//        //then
//        assertThat(email).isEqualTo("tes**@gmail.com");
//    }
//
//}
