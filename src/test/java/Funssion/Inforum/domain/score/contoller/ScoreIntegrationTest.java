package Funssion.Inforum.domain.score.contoller;

import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.memo.service.MemoService;
import Funssion.Inforum.domain.score.Rank;
import Funssion.Inforum.domain.score.dto.ScoreRank;
import Funssion.Inforum.domain.score.dto.UserInfoWithScoreRank;
import Funssion.Inforum.domain.score.repository.ScoreRepository;
import Funssion.Inforum.domain.score.service.ScoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class ScoreIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired
    ScoreService scoreService;
    @Autowired
    ScoreRepository scoreRepository;
    @Autowired
    MemoService memoService;
    @Autowired
    MemoRepository memoRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ObjectMapper objectMapper;
    static String userId;
    @BeforeEach
    void saveUserAndCreateOneMemo(){
        SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(MemberSaveDto.builder()
                .userName("testUser")
                .userPw("a1234567")
                .userEmail("testUser@gmail.com")
                .loginType(LoginType.NON_SOCIAL)
                .build()));
        userId = saveMemberResponseDto.getId().toString();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userId.toString(),"12345678"));
        memoService.createMemo(MemoSaveDto.builder()
                .memoTitle("Hi")
                .memoTags(List.of("tag1","tag2"))
                .memoDescription("hello")
                .memoText("\"{\\\"type\\\": \\\"doc\\\", \\\"content\\\": [{\\\"type\\\": \\\"paragraph\\\", \\\"content\\\": [{\\\"text\\\": \\\"메모내용\\\", \\\"type\\\": \\\"text\\\"}]}]}\"")
                .memoColor("yellow")
                .isTemporary(false)
                .build());
    }

    @Test
    @DisplayName("유저의 rank와 score를 성공적으로 조회하는 경우")
    void getUserScoreAndRank() throws Exception {
        MvcResult result = mvc.perform(get("/score/" + userId)
                        .with(user(String.valueOf(userId))))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        ScoreRank scoreRank = objectMapper.readValue(responseBody, ScoreRank.class);
        assertThat(scoreRank.getRank()).isEqualTo(Rank.BRONZE_5);
        assertThat(scoreRank.getScore()).isEqualTo(ScoreType.MAKE_MEMO.getScore());

    }
    @Test
    @DisplayName("로그인 하지 않는 유저가 score를 성공적으로 조회하는 경우")
    void getUnAuthorizedUserGetHisScoreAndRank() throws Exception {
        MvcResult result = mvc.perform(get("/score/" + -1))
                .andExpect(status().isUnauthorized())
                .andReturn();
        assertThat(result.getResolvedException().getMessage()).isEqualTo("해당 유저가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("Top 10 유저 가져오기")
    void getTop10Users() throws Exception {
        memberRepository.save(makeNonSocialMemberDao("name1", "test1@gmail.com"));
        memberRepository.save(makeNonSocialMemberDao("name2", "test2@gmail.com"));
        memberRepository.save(makeNonSocialMemberDao("name3", "test3@gmail.com"));
        memberRepository.save(makeNonSocialMemberDao("name4", "test4@gmail.com"));
        memberRepository.save(makeNonSocialMemberDao("name5", "test5@gmail.com"));
        memberRepository.save(makeNonSocialMemberDao("name6", "test6@gmail.com"));
        memberRepository.save(makeNonSocialMemberDao("name7", "test7@gmail.com"));
        memberRepository.save(makeNonSocialMemberDao("name8", "test8@gmail.com"));
        memberRepository.save(makeNonSocialMemberDao("name9", "test9@gmail.com"));
        memberRepository.save(makeNonSocialMemberDao("name10", "test10@gmail.com"));
        
        MvcResult result = mvc.perform(get("/score/rank"))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        List<UserInfoWithScoreRank> listOfUsers = JsonPath.read(responseBody, "$[*]");
        assertThat(listOfUsers.size()).isEqualTo(10);
    }

    private static NonSocialMember makeNonSocialMemberDao(String name1, String mail) {
        return NonSocialMember.createNonSocialMember(MemberSaveDto.builder()
                .userName(name1)
                .userPw("a1234567")
                .userEmail(mail)
                .loginType(LoginType.NON_SOCIAL)
                .build());
    }
}