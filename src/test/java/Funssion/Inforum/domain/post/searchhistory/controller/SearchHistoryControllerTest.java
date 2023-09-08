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

}
