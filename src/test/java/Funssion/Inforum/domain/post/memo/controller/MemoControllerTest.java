package Funssion.Inforum.domain.post.memo.controller;

import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.service.MemoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemoController.class)
class MemoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private static MemoService memoService;

    private static String rightRequest;
    private static String noTitleRequest;
    private static String noDescriptionRequest;
    private static String noTextRequest;
    private static String noColorRequest;

    @BeforeAll
    static void init() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        rightRequest = mapper.writeValueAsString(MemoSaveDto.builder()
                .memoTitle("Hi")
                .memoDescription("hello")
                .memoText("my name is ...")
                .memoColor("yellow")
                .isTemporary(true)
                .build());

        noTitleRequest = mapper.writeValueAsString(MemoSaveDto.builder()
                .memoDescription("hello")
                .memoText("my name is ...")
                .memoColor("yellow")
                .isTemporary(true)
                .build());

        noDescriptionRequest = mapper.writeValueAsString(MemoSaveDto.builder()
                .memoTitle("hi")
                .memoText("my name is ...")
                .memoColor("yellow")
                .isTemporary(true)
                .build());

        noTextRequest = mapper.writeValueAsString(MemoSaveDto.builder()
                .memoTitle("hi")
                .memoDescription("hello")
                .memoColor("yellow")
                .isTemporary(true)
                .build());

        noColorRequest = mapper.writeValueAsString(MemoSaveDto.builder()
                .memoTitle("Hi")
                .memoDescription("hello")
                .memoText("my name is ...")
                .isTemporary(true)
                .build());
    }

    @Test
    @WithMockUser
    @DisplayName("메인 페이지 메모 불러오기")
    void getMemoList() throws Exception {

        mvc.perform(get("/memos"))
                .andExpect(status().isOk());

        mvc.perform(get("/memos")
                        .param("period", "DAY")
                        .param("orderBy", "HOT"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("메모 등록하기")
    void addMemo() throws Exception {

        mvc.perform(post("/memos")
                        .contentType(APPLICATION_JSON)
                        .content(rightRequest)
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("잘못된 메모 등록하기")
    void badMemoRequest() throws Exception {

        mvc.perform(post("/memos")
                        .contentType(APPLICATION_JSON)
                        .content(noTitleRequest)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/memos")
                        .contentType(APPLICATION_JSON)
                        .content(noDescriptionRequest)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/memos")
                        .contentType(APPLICATION_JSON)
                        .content(noTextRequest)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/memos")
                        .contentType(APPLICATION_JSON)
                        .content(noColorRequest)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("메모 세부 내용 불러오기")
    void getMemoDetails() throws Exception {
        mvc.perform(get("/memos/1"))
                .andExpect(status().isOk());

        mvc.perform(get("/memos/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("메모 수정하기")
    void modifyMemo() throws Exception {
        mvc.perform(post("/memos/1")
                        .contentType(APPLICATION_JSON)
                        .content(rightRequest)
                        .with(csrf()))
                .andExpect(status().isOk());

        mvc.perform(post("/memos/-5")
                        .contentType(APPLICATION_JSON)
                        .content(rightRequest)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/memos/1")
                        .contentType(APPLICATION_JSON)
                        .content(noColorRequest)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("메모 삭제하기")
    void removeMemo() throws Exception {
        mvc.perform(delete("/memos/3")
                        .with(csrf()))
                .andExpect(status().isOk());

        mvc.perform(delete("/memos/-3")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("메모 검색하기")
    void getSearchedMemos() throws Exception {
        mvc.perform(get("/memos/search")
                        .param("searchString", "JPA")
                        .param("orderBy", "hot")
                        .param("isRecoded", "true")
                        .param("isTag", "true"))
                .andExpect(status().isOk());

        mvc.perform(get("/memos/search")
                        .param("searchString", "JPA")
                        .param("isRecoded", "true")
                        .param("isTag", "true"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/memos/search")
                        .param("searchString", "JPA")
                        .param("orderBy", "hot")
                        .param("isTag", "true"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/memos/search")
                        .param("searchString", "JPA")
                        .param("orderBy", "hot")
                        .param("isRecoded", "true"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/memos/search")
                        .param("searchString", "")
                        .param("orderBy", "hot")
                        .param("isRecoded", "true")
                        .param("isTag", "true"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/memos/search")
                        .param("searchString", "JPA")
                        .param("orderBy", "hotty")
                        .param("isRecoded", "true")
                        .param("isTag", "true"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/memos/search")
                        .param("searchString", "JPA")
                        .param("orderBy", "hot")
                        .param("isRecoded", "true!")
                        .param("isTag", "true"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/memos/search")
                        .param("searchString", "JPA")
                        .param("orderBy", "hot")
                        .param("isRecoded", "true")
                        .param("isTag", "true!"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("임시 메모 불러오기")
    void getDraftMemos() throws Exception {
        mvc.perform(get("/memos/drafts"))
                .andExpect(status().isOk());
    }
}