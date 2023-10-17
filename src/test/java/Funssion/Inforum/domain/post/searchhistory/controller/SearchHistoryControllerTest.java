package Funssion.Inforum.domain.post.searchhistory.controller;

import Funssion.Inforum.domain.post.searchhistory.dto.response.SearchHistoryDto;
import Funssion.Inforum.domain.post.searchhistory.service.SearchHistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchHistoryController.class)
public class SearchHistoryControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    SearchHistoryService service;

    @Test
    @WithMockUser
    @DisplayName("검색 기록 조회하기")
    void getRecentSearchHistoryTop10() throws Exception {
        mvc.perform(get("/search/history"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("검색 기록 저장하기")
    void addSearchHistory() throws Exception {
        mvc.perform(post("/search/history")
                        .param("searchString", "JPA")
                        .param("isTag", "true")
                        .with(csrf()))
                .andExpect(status().isCreated());

        mvc.perform(post("/search/history")
                        .param("searchString", "")
                        .param("isTag", "false")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/search/history")
                        .param("searchString", " ")
                        .param("isTag", "false")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/search/history")
                        .param("isTag", "true")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/search/history")
                        .param("searchString", "JPA")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/search/history")
                        .param("searchString", "JPA")
                        .param("isTag", "true!!")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("검색 기록 삭제하기")
    void removeSearchHistory() throws Exception {
        mvc.perform(delete("/search/history/4")
                .with(csrf()))
                .andExpect(status().isOk());


        mvc.perform(delete("/search/history/-4")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("검색 기록 수정하기")
    void updateSearchHistory() throws Exception {
        mvc.perform(post("/search/history/4")
                        .with(csrf()))
                .andExpect(status().isOk());


        mvc.perform(post("/search/history/-4")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

}
