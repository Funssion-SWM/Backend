package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
class ScoreIntegrationTest {
    @Autowired
    ScoreRepository scoreRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ScoreService scoreService;
    @Autowired
    MyRepository myRepository;
    static Long saveMemberId;

    @BeforeEach
    void saveUser(){
        saveUser("username");
    }
    private void saveUser(String name) {
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .userName(name)
                .loginType(LoginType.NON_SOCIAL)
                .userPw("a1234567!")
                .userEmail(name+"@gmail.com")
                .build();
        MemberProfileEntity memberProfileEntity = MemberProfileEntity.builder()
                .nickname(name)
                .profileImageFilePath("taehoon-image")
                .introduce("introduce of taehoon")
                .userTags(List.of("tag1", "tag2"))
                .build();

        SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(memberSaveDto));
        saveMemberId = saveMemberResponseDto.getId();
        myRepository.createProfile(saveMemberId, memberProfileEntity);
    }
    @Nested
    @DisplayName("유저의 Score가 증가하는 경우")
    class addScoreOfUser{
        Long postId = 1L;
        @Test
        @DisplayName("유저가 일별 최대 점수를 채우지 않았을 경우")
        void addScoreWhenNotOverDailyLimit(){
            assertThat(scoreService.checkUserDailyScoreAndAdd(saveMemberId, ScoreType.MAKE_MEMO,postId)).isEqualTo(ScoreType.MAKE_MEMO.getScore());
        }
    }

}