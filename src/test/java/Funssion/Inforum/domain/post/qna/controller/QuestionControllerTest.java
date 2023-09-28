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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(QuestionController.class)
class QuestionControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    QuestionService questionService;

    static String createValidQuestionRequest;
    static String createInvalidQuestionRequest;

    static QuestionSaveDto questionSaveObject;
    static QuestionSaveDto invalidQuestionSaveObject;
    static final String AUTHORIZED_USER = "1";
    @BeforeAll
    static void init() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        questionSaveObject = QuestionSaveDto.builder()
                .title("Question 시험용 제목입니다.")
                .text("Question 시험용 내용입니다.")
                .tags(List.of("태그1", "태그2"))
                .build();
        invalidQuestionSaveObject = QuestionSaveDto.builder()
                .title("")
                .text("Question 시험용 내용입니다.")
                .tags(List.of("태그1", "태그2"))
                .build();

        createValidQuestionRequest = objectMapper.writeValueAsString(questionSaveObject);
        createInvalidQuestionRequest = objectMapper.writeValueAsString(invalidQuestionSaveObject);
    }
    @Nested
    @DisplayName("질문 CRUD")
    class QuestionCRUD {
        @Nested
        @DisplayName("질문 생성하기")
        class createQuestion{

            @Test
            //username은 UserDetailsService에서 userId를 username으로 지정하였음.
            @WithMockUser(username = AUTHORIZED_USER)
            @DisplayName("로그인한 유저가 올바른 질문을 생성")
            void createQuestionByAuthUser() throws Exception {
                mvc.perform(post("/questions")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createValidQuestionRequest))
                        .andExpect(status().isCreated());
            }
            @Test
            @WithMockUser(username = AUTHORIZED_USER)
            @DisplayName("로그인한 유저가 특정 메모랑 연관된 올바른 질문을 생성")
            void createQuestionInMemoByAuthUser() throws Exception {
                mvc.perform(post("/questions")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createValidQuestionRequest)
                                .param("memoId","1"))
                        .andExpect(status().isCreated());
            }

            @Test
            //username은 UserDetailsService에서 userId를 username으로 지정하였음.
            @WithMockUser(username = AUTHORIZED_USER)
            @DisplayName("로그인한 유저가 제목이 없는 질문을 생성")
            void createInvalidQuestionByAuthUser() throws Exception {
                mvc.perform(post("/questions")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createInvalidQuestionRequest))
                        .andExpect(status().isBadRequest());
            }
            @Test
            @WithMockUser(username = SecurityContextUtils.ANONYMOUS_USER_ID_STRING)
            @DisplayName("로그인하지 않은 유저가 올바르지 않은 질문을 생성")
            void createQuestionByNonAuthUser() throws Exception {
                mvc.perform(post("/questions")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createValidQuestionRequest))
                        .andExpect(status().isUnauthorized());
            }
        }
        @Nested
        @DisplayName("질문 받아오기")
        class readQuestion{
            @Nested
            @DisplayName("Request Parameter 검증")
            class orderTypeParameter{

                @Test
                @WithMockUser
                @DisplayName("잘못된 파라미터 입력")
                void badRequestUrlParam() throws Exception{
                    mvc.perform(get("/questions")
                            .param("orderBy","NEW1"))
                            .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser
                @DisplayName("정렬 파라미터 검증")
                void getLatestQuestion() throws Exception {
                    mvc.perform(get("/questions"))
                            .andExpect(status().isOk());
                    mvc.perform(get("/questions")
                            .param("orderBy","NEW"))
                            .andExpect(status().isOk());

                    mvc.perform(get("/questions")
                                    .param("orderBy","HOT"))
                            .andExpect(status().isOk());

                    mvc.perform(get("/questions")
                                    .param("orderBy","ANSWERS"))
                            .andExpect(status().isOk());

                    mvc.perform(get("/questions")
                                    .param("orderBy","SOLVED"))
                            .andExpect(status().isOk());
                }
            }
        }
        @Nested
        @DisplayName("질문 수정하기")
        class updateQuestion {
            @Test
            @DisplayName("해당 게시물에 권한 있는 유저가 질문을 수정")
            @WithMockUser(username=AUTHORIZED_USER)
            void authorizedUserUpdateQuestion() throws Exception {
                Long targetQuestionId = 1L;
                when(questionService.getAuthorId(targetQuestionId)).thenReturn(Long.valueOf(AUTHORIZED_USER));

                mvc.perform(put("/questions/"+ targetQuestionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createValidQuestionRequest))
                        .andExpect(status().isOk());
            }
            @Test
            @DisplayName("해당 게시물에 권한 없는 유저가 질문을 수정")
            @WithMockUser(SecurityContextUtils.ANONYMOUS_USER_ID_STRING)
            void unAuthorizedUserUpdateQuestion() throws Exception{
                Long targetQuestionId = 1L;
                when(questionService.getAuthorId(targetQuestionId)).thenReturn(Long.valueOf(AUTHORIZED_USER));
                mvc.perform(put("/questions/"+ targetQuestionId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createValidQuestionRequest))
                        .andExpect(status().isUnauthorized());
            }
        }
        @Nested
        @DisplayName("질문 삭제하기")
        class deleteQuestion{
            @Test
            @DisplayName("해당 게시물에 권한 있는 유저가 질문을 삭제")
            @WithMockUser(username=AUTHORIZED_USER)
            void authorizedUserDeleteQuestion() throws Exception {
                Long targetQuestionId = 1L;
                when(questionService.getAuthorId(targetQuestionId)).thenReturn(Long.valueOf(AUTHORIZED_USER));

                mvc.perform(delete("/questions/"+ targetQuestionId)
                        .with(csrf()))
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("해당 게시물에 권한 없는 유저가 질문을 삭제")
            @WithMockUser(SecurityContextUtils.ANONYMOUS_USER_ID_STRING)
            void unAuthorizedUserDeleteQuestion() throws Exception {
                Long targetQuestionId = 1L;
                when(questionService.getAuthorId(targetQuestionId)).thenReturn(Long.valueOf(AUTHORIZED_USER));

                mvc.perform(delete("/questions/"+ targetQuestionId)
                        .with(csrf()))
                        .andExpect(status().isUnauthorized());
            }
        }
    }
}
