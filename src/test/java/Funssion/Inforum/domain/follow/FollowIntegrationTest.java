package Funssion.Inforum.domain.follow;

import Funssion.Inforum.domain.follow.domain.Follow;
import Funssion.Inforum.domain.follow.repository.FollowRepository;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class FollowIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FollowRepository followRepository;

    SaveMemberResponseDto savedMember1;
    SaveMemberResponseDto savedMember2;
    SaveMemberResponseDto savedMember3;

    String testUserId1;
    String testUserId2;
    String testUserId3;

    @BeforeEach
    void init() {
         savedMember1 = memberRepository.save(NonSocialMember.builder()
                .userPw("1234")
                .userEmail("nonsocial@gmail.com")
                .loginType(LoginType.NON_SOCIAL)
                .authId(1L)
                .userName("jinu")
                .introduce("hi")
                .createdDate(LocalDateTime.now())
                .tags("Java")
                .imagePath("https://image")
                .build());

        savedMember2 = memberRepository.save(SocialMember.builder()
                .userEmail("social@gmail.com")
                .loginType(LoginType.SOCIAL)
                .userName("jinu2")
                .introduce("hi")
                .createdDate(LocalDateTime.now())
                .tags("Java")
                .imagePath("https://image2")
                .build());

        savedMember3 = memberRepository.save(SocialMember.builder()
                .userEmail("social2@gmail.com")
                .loginType(LoginType.SOCIAL)
                .userName("jinu3")
                .introduce("hill")
                .createdDate(LocalDateTime.now())
                .tags("Spring")
                .imagePath("https://image3")
                .build());

        testUserId1 = savedMember1.getId().toString();
        testUserId2 = savedMember2.getId().toString();
        testUserId3 = savedMember3.getId().toString();
    }

    @Nested
    @DisplayName("유저 팔로우 하기")
    class follow {

        @Test
        @DisplayName("일반 케이스")
        void success() throws Exception {
            mvc.perform(post("/follow")
                    .with(user(testUserId1))
                    .param("userId", testUserId2));

            mvc.perform(get("/users/profile/" + testUserId1))
                    .andExpect(content().string(containsString("\"followCnt\":1")))
                    .andExpect(content().string(containsString("\"followerCnt\":0")));

            mvc.perform(get("/users/profile/" + testUserId2))
                    .andExpect(content().string(containsString("\"followCnt\":0")))
                    .andExpect(content().string(containsString("\"followerCnt\":1")));
        }

        @Test
        @DisplayName("이미 팔로우한 유저를 다시 팔로우")
        void followAlreadyFollowed() throws Exception {
            mvc.perform(post("/follow")
                    .with(user(testUserId1))
                    .param("userId", testUserId2));

            mvc.perform(post("/follow")
                    .with(user(testUserId1))
                    .param("userId", testUserId2))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("자기 자신을 팔로우")
        void followSelf() throws Exception {
            mvc.perform(post("/follow")
                    .with(user(testUserId1))
                    .param("userId", testUserId1))
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("유저 팔로우 취소하기")
    class unfollow {

        @Test
        @DisplayName("일반 케이스")
        void success() throws Exception {
            mvc.perform(post("/follow")
                    .with(user(testUserId1))
                    .param("userId", testUserId2));

            mvc.perform(post("/unfollow")
                    .with(user(testUserId1))
                    .param("userId", testUserId2));

            mvc.perform(get("/users/profile/" + testUserId1))
                    .andExpect(content().string(containsString("\"followCnt\":0")))
                    .andExpect(content().string(containsString("\"followerCnt\":0")));

            mvc.perform(get("/users/profile/" + testUserId2))
                    .andExpect(content().string(containsString("\"followCnt\":0")))
                    .andExpect(content().string(containsString("\"followerCnt\":0")));
        }

        @Test
        @DisplayName("팔로우 하지 않은 유저를 팔로우 취소")
        void unfollowWithoutFollow() throws Exception {
            mvc.perform(post("/unfollow")
                    .with(user(testUserId1))
                    .param("userId", testUserId2))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("자기 자신을 팔로우 취소")
        void unfollowSelf() throws Exception {
            mvc.perform(post("/unfollow")
                            .with(user(testUserId2))
                            .param("userId", testUserId2))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @Transactional
    @DisplayName("팔로우, 팔로잉 유저 정보 조회")
    class getUserProfilesByFollowInfo {

        @BeforeEach
        void init() {
            followRepository.save(Follow.builder()
                    .userId(savedMember1.getId())
                    .followedUserId(savedMember2.getId())
                    .build());

            followRepository.save(Follow.builder()
                    .userId(savedMember1.getId())
                    .followedUserId(savedMember3.getId())
                    .build());

            followRepository.save(Follow.builder()
                    .userId(savedMember3.getId())
                    .followedUserId(savedMember2.getId())
                    .build());
        }

        @Test
        @DisplayName("팔로우한 유저 정보 조회하기")
        void getFollowingUserProfiles() throws Exception {
            mvc.perform(get("/follows")
                    .param("userId", testUserId1))
                    .andExpect(content().string(containsString("\"nickname\":" + toJsonString(savedMember2.getName()))))
                    .andExpect(content().string(containsString("\"nickname\":" + toJsonString(savedMember3.getName()))));

            mvc.perform(get("/follows")
                            .param("userId", testUserId2))
                    .andExpect(content().string("[]"));

            mvc.perform(get("/follows")
                            .param("userId", testUserId3))
                    .andExpect(content().string(containsString("\"nickname\":" + toJsonString(savedMember2.getName()))));
        }

        @Test
        @DisplayName("팔로우한 유저 정보 조회하기")
        void getFollowerUserProfiles() throws Exception {
            mvc.perform(get("/followers")
                            .param("userId", testUserId1))
                            .andExpect(content().string("[]"));

            mvc.perform(get("/followers")
                            .param("userId", testUserId2))
                    .andExpect(content().string(containsString("\"nickname\":" + toJsonString(savedMember1.getName()))))
                    .andExpect(content().string(containsString("\"nickname\":" + toJsonString(savedMember3.getName()))));

            mvc.perform(get("/followers")
                            .param("userId", testUserId3))
                    .andExpect(content().string(containsString("\"nickname\":" + toJsonString(savedMember1.getName()))));
        }
    }

    String toJsonString(String str) {
        return "\"" + str + "\"";
    }
}
