package Funssion.Inforum.domain.mypage.controller;

import Funssion.Inforum.domain.mypage.service.MyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MyController.class)
class MyControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MyService myService;

    @Test
    @WithMockUser
    @DisplayName("유저 정보 가져오기")
    void getUserInfo() throws Exception {
        mvc.perform(get("/mypage/1"))
                .andExpect(status().isOk());

        mvc.perform(get("/mypage/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("해당 유저 메모 가져오기")
    void getMyMemos() throws Exception {
        mvc.perform(get("/mypage/1/memos"))
                .andExpect(status().isOk());

        mvc.perform(get("/mypage/-1/memos"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("해당 유저가 좋아요한 메모 가져오기")
    void getMyLikedMemos() throws Exception {
        mvc.perform(get("/mypage/1/memos/liked"))
                .andExpect(status().isOk());

        mvc.perform(get("/mypage/-1/memos/liked"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("해당 유저 임시 메모 가져오기")
    void getMyDraftMemos() throws Exception {
        mvc.perform(get("/mypage/1/memos/drafts"))
                .andExpect(status().isOk());

        mvc.perform(get("/mypage/-1/memos/drafts"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("해당 유저 히스토리 가져오기")
    void getHistory() throws Exception {
        mvc.perform(get("/mypage/1/history")
                .param("year", "2023")
                .param("month", "10"))
                .andExpect(status().isOk());

        mvc.perform(get("/mypage/-1/history")
                        .param("year", "2023")
                        .param("month", "10"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/mypage/1/history")
                        .param("year", "2023")
                        .param("month", "13"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/mypage/1/history")
                        .param("year", "2023")
                        .param("month", "0"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/mypage/1/history")
                        .param("year", "-30")
                        .param("month", "10"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/mypage/1/history")
                        .param("month", "10"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/mypage/1/history")
                        .param("year", "2023"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/mypage/1/history"))
                .andExpect(status().isBadRequest());
    }
}