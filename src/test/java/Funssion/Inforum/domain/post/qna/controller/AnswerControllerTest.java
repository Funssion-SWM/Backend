package Funssion.Inforum.domain.post.qna.controller;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.domain.post.qna.service.AnswerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnswerController.class)
class AnswerControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    AnswerService answerService;

    static final String AUTHORIZED_USER = "1";
    static final String UN_AUTHORIZED_USER = "999";
    static String answerSaveDtoRequest;
    @BeforeAll
    static void init() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        AnswerSaveDto answerSaveDto = AnswerSaveDto.builder()
                .text("답변의 내용이 들어갑니다.")
                .build();
        answerSaveDtoRequest = objectMapper.writeValueAsString(answerSaveDto);
    }

    @Nested
    @DisplayName("답변 CRUD")
    class answerCRUD{
        @Nested
        @DisplayName("답변 생성")
        class createAnswer{
            @Test
            @DisplayName("로그인한 유저가 답변을 생성합니다")
            @WithMockUser(username=AUTHORIZED_USER)
            void createAnswerOfQuestion() throws Exception {
                mvc.perform(post("/answers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("questionId","1")
                        .content(answerSaveDtoRequest))
                        .andExpect(status().isCreated());
            }

            @Test
            @DisplayName("로그인하지 않은 유저가 답변을 생성합니다")
            @WithMockUser(username= SecurityContextUtils.ANONYMOUS_USER_ID_STRING)
            void createAnswerOfQuestionByAnonymousUser() throws Exception {
                mvc.perform(post("/answers")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("questionId","1")
                                .content(answerSaveDtoRequest))
                        .andExpect(status().isUnauthorized());
            }
        }
        @Nested
        @DisplayName("답변 가져오기")
        class getAnswer{
            @Test
            @DisplayName("짊문과 연관된 여러 질문정보들을 리스트로 가져온후, 자신이 작성한 글의 여부를 확인합니다.")
            @WithMockUser(username = AUTHORIZED_USER)
            void getAnswersOfQuestion() throws Exception {
                String questionId = "1";
                Long loginId = 1L;
                Long longTypeQuestionId = Long.valueOf(questionId);
                List<Answer> answers = mockAnswerDomainListIn(longTypeQuestionId);

                when(answerService.getAnswersOfQuestion(loginId ,longTypeQuestionId)).thenReturn(answers);
                MvcResult result = mvc.perform(get("/answers")
                                .param("questionId", questionId))
                        .andExpect(status().isOk())
                        .andReturn();
                String responseBody = result.getResponse().getContentAsString();
                List<Boolean> isAnswerElementIsMine = JsonPath.read(responseBody, "$[*].mine");
                assertThat(isAnswerElementIsMine).containsExactly(true,false,false);
            }

            @Test
            @WithMockUser(username=AUTHORIZED_USER)
            @DisplayName("답변 고유 id로 한가지 답변만 가져왔는데, 자신의 게시글일 때")
            void getMyAnswerByQuestionId() throws Exception {
                String questionId = "1";
                Long longTypeQuestionId = Long.valueOf(questionId);

                Answer answerDomain = Answer.builder()
                        .questionId(Long.valueOf(questionId))
                        .id(1L)
                        .authorName("1번답변작성자이룸")
                        .authorImagePath("1번답변작성자이미지경로")
                        .authorId(Long.valueOf(AUTHORIZED_USER))
                        .createdDate(LocalDateTime.now())
                        .updatedDate(LocalDateTime.now())
                        .isSelected(false)
                        .likes(0L)
                        .repliesCount(0L)
                        .text("답변 내용")
                        .build();
                when(answerService.getAnswerBy(longTypeQuestionId)).thenReturn(answerDomain);

                MvcResult result = mvc.perform(get("/answers/" + questionId))
                        .andExpect(status().isOk())
                        .andReturn();
                String responseBody = result.getResponse().getContentAsString();
                Boolean isAnswerIsMine = JsonPath.read(responseBody, "$.mine");
                assertThat(isAnswerIsMine).isEqualTo(true);
            }
            @Test
            @WithMockUser(username=UN_AUTHORIZED_USER)
            @DisplayName("권한없는 유저가 답변 고유 id로 한가지 답변만 가져온 경우")
            void getAnswerByQuestionId() throws Exception {
                String questionId = "1";
                Long longTypeQuestionId = Long.valueOf(questionId);

                Answer answerDomain = Answer.builder()
                        .questionId(Long.valueOf(questionId))
                        .id(1L)
                        .authorName("1번답변작성자이룸")
                        .authorImagePath("1번답변작성자이미지경로")
                        .authorId(Long.valueOf(AUTHORIZED_USER))
                        .createdDate(LocalDateTime.now())
                        .updatedDate(LocalDateTime.now())
                        .isSelected(false)
                        .likes(0L)
                        .repliesCount(0L)
                        .text("답변 내용")
                        .build();
                when(answerService.getAnswerBy(longTypeQuestionId)).thenReturn(answerDomain);

                MvcResult result = mvc.perform(get("/answers/" + questionId))
                        .andExpect(status().isOk())
                        .andReturn();
                String responseBody = result.getResponse().getContentAsString();
                Boolean isAnswerIsMine = JsonPath.read(responseBody, "$.mine");
                assertThat(isAnswerIsMine).isEqualTo(false);
            }
        }

        @Nested
        @DisplayName("답변 수정하기")
        class updateAnswer{
            @Test
            @WithMockUser(username=AUTHORIZED_USER)
            @DisplayName("권한이 있는 유저가 답변을 수정합니다.")
            void updateAnswerAuthorized() throws Exception {
                Long savedAnswerId = 1L;
                when(answerService.getAuthorId(savedAnswerId)).thenReturn(Long.valueOf(AUTHORIZED_USER));
                mvc.perform(patch("/answers/"+savedAnswerId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(answerSaveDtoRequest))
                        .andExpect(status().isOk());
            }
        }
    }

        private List<Answer> mockAnswerDomainListIn(Long questionId){
            Answer answerDomain1 = Answer.builder()
                    .questionId(Long.valueOf(questionId))
                    .id(1L)
                    .authorName("1번답변작성자이룸")
                    .authorImagePath("1번답변작성자이미지경로")
                    .authorId(Long.valueOf(AUTHORIZED_USER))
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .isSelected(false)
                    .likes(0L)
                    .repliesCount(0L)
                    .text("답변 내용")
                    .build();
            Answer answerDomain2 = Answer.builder()
                    .questionId(Long.valueOf(questionId))
                    .id(1L)
                    .authorName("2번답변작성자이룸")
                    .authorImagePath("2번답변작성자이미지경로")
                    .authorId(2L)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .isSelected(false)
                    .likes(0L)
                    .repliesCount(0L)
                    .text("답변 내용")
                    .build();
            Answer answerDomain3 = Answer.builder()
                    .questionId(Long.valueOf(questionId))
                    .id(1L)
                    .authorName("3번답변작성자이룸")
                    .authorImagePath("3번답변작성자이미지경로")
                    .authorId(3L)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .isSelected(false)
                    .likes(0L)
                    .repliesCount(0L)
                    .text("답변 내용")
                    .build();
            return List.of(answerDomain1, answerDomain2, answerDomain3);
        }
    }
