package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestionIntegrationTest {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    MyRepository myRepository;
    @Autowired
    QuestionService questionService;
    @Autowired
    MemberRepository memberRepository;

    static Long saveMemberId;
    @BeforeAll
    void saveMember() {
        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .userName("taehoon")
                .loginType(LoginType.NON_SOCIAL)
                .userPw("a1234567!")
                .userEmail("test@gmail.com")
                .build();
        MemberProfileEntity memberProfileEntity = MemberProfileEntity.builder()
                .nickname("taehoon")
                .profileImageFilePath("taehoon-image")
                .introduce("introduce of taehoon")
                .userTags(List.of("tag1", "tag2"))
                .build();

        SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(memberSaveDto));
        saveMemberId = saveMemberResponseDto.getId();
        myRepository.createProfile(saveMemberId, memberProfileEntity);
    }

    @Test
    @DisplayName("질문 생성")
    @Transactional
    void createQuestion(){
        QuestionSaveDto questionSaveDto = QuestionSaveDto.builder().title("테스트 제목")
                .text("질문 내용")
                .tags(List.of("tag1", "tag2"))
                .build();
        Assertions.assertThatCode(()->questionService.createQuestion(questionSaveDto,saveMemberId)).doesNotThrowAnyException();
    }

}