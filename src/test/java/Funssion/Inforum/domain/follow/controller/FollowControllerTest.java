package Funssion.Inforum.domain.follow.controller;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.follow.service.FollowService;
import Funssion.Inforum.domain.post.memo.service.MemoService;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FollowController.class)
class FollowControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private static FollowService followService;

    @Test
    @DisplayName("유저 팔로우 하기")
    void followUser() throws Exception {
        mvc.perform(post("/follow")
                        .with(user("1"))
                        .with(csrf())
                        .param("userId", "2"))
                .andExpect(status().isOk());

        mvc.perform(post("/follow")
                        .with(user("1"))
                        .with(csrf())
                        .param("userId", "1"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/follow")
                        .with(user("1"))
                        .with(csrf())
                        .param("userId", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유저 팔로우 취소하기")
    void unfollowUser() throws Exception {
        mvc.perform(post("/unfollow")
                        .with(user("1"))
                        .with(csrf())
                        .param("userId", "2"))
                .andExpect(status().isOk());

        mvc.perform(post("/unfollow")
                        .with(user("1"))
                        .with(csrf())
                        .param("userId", "1"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/unfollow")
                        .with(user("1"))
                        .with(csrf())
                        .param("userId", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우한 유저 조회하기")
    void getFollowUsersInfo() throws Exception {
        mvc.perform(get("/follows")
                .param("userId", "1"))
                .andExpect(status().isOk());

        mvc.perform(get("/follows")
                .param("userId", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("팔로워 유저 조회하기")
    void getFollowerUsersInfo() throws Exception {
        mvc.perform(get("/followers")
                        .param("userId", "1"))
                .andExpect(status().isOk());

        mvc.perform(get("/followers")
                        .param("userId", "0"))
                .andExpect(status().isBadRequest());
    }
}