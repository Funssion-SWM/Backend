package Funssion.Inforum.domain.post.qna.controller;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.service.QuestionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(QuestionController.class)
class QuestionControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    QuestionService questionService;

    static String createValidQuestionRequest;
    static String createInvalidQuestionRequest;

    static final String AUTHORIZED_USER = "1";
    @BeforeAll
    static void init() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        createValidQuestionRequest = objectMapper.writeValueAsString(QuestionSaveDto.builder()
                .title("Question 시험용 제목입니다.")
                .text("Question 시험용 내용입니다.")
                .tags(List.of("태그1", "태그2"))
                .build());
        createInvalidQuestionRequest = objectMapper.writeValueAsString(QuestionSaveDto.builder()
                .title("")
                .text("Question 시험용 내용입니다.")
                .tags(List.of("태그1", "태그2"))
                .build());
    }
    @Nested
    @DisplayName("질문 CRUD")
    class QuestionCRUD {
        @Nested
        @DisplayName("질문 생성")
        class createQuestion{

            @Test
            //username은 UserDetailsService에서 userId를 username으로 지정하였음.
            @WithMockUser(username = AUTHORIZED_USER)
            @DisplayName("로그인한 유저가 올바른 질문을 생성")
            void createQuestionByAuthUser() throws Exception {
                mvc.perform(post("/question")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createValidQuestionRequest))
                        .andExpect(status().isCreated());
            }

            @Test
            //username은 UserDetailsService에서 userId를 username으로 지정하였음.
            @WithMockUser(username = AUTHORIZED_USER)
            @DisplayName("로그인한 유저가 제목이 없는 질문을 생성")
            void createInvalidQuestionByAuthUser() throws Exception {
                mvc.perform(post("/question")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createInvalidQuestionRequest))
                        .andExpect(status().isBadRequest());
            }
            @Test
            @WithMockUser(username = SecurityContextUtils.ANONYMOUS_USER_ID_STRING)
            @DisplayName("로그인하지 않은 유저가 올바르지 않은 질문을 생성")
            void createQuestionByNonAuthUser() throws Exception {
                mvc.perform(post("/question")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createValidQuestionRequest))
                        .andExpect(status().isUnauthorized());
            }
        }
    }
}