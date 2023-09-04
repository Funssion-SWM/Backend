package Funssion.Inforum.domain.like.controller;

import Funssion.Inforum.domain.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.like.service.LikeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    LikeService likeService;

    @Test
    @WithMockUser
    @DisplayName("게시글 좋아요 정보 가져오기")
    void getLikeInfo() throws Exception {
        mvc.perform(get("/memos/1/like"))
                .andExpect(status().isOk());

        mvc.perform(get("/blogs/1/like"))
                .andExpect(status().isOk());

        mvc.perform(get("/questions/1/like"))
                .andExpect(status().isOk());

        mvc.perform(get("/questions/-1/like"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/memodsfsd/1/like"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 좋아요 하기")
    void like() throws Exception {
        mvc.perform(post("/memos/1/like")
                        .with(csrf()))
                .andExpect(status().isOk());

        mvc.perform(post("/blogs/1/like")
                        .with(csrf()))
                .andExpect(status().isOk());

        mvc.perform(post("/questions/1/like")
                        .with(csrf()))
                .andExpect(status().isOk());

        mvc.perform(post("/questions/-1/like")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/memodsfsd/1/like")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 좋아요 취소하기")
    void unlike() throws Exception {
        mvc.perform(post("/memos/1/unlike")
                        .with(csrf()))
                .andExpect(status().isOk());

        mvc.perform(post("/blogs/1/unlike")
                        .with(csrf()))
                .andExpect(status().isOk());

        mvc.perform(post("/questions/1/unlike")
                        .with(csrf()))
                .andExpect(status().isOk());

        mvc.perform(post("/questions/-1/unlike")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/memodsfsd/1/unlike")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}