package Funssion.Inforum.domain.post.like;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.member.service.MemberService;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.domain.Post;
import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.like.service.LikeService;
import Funssion.Inforum.domain.post.qna.Constant;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.post.qna.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@Transactional
public class LIkeIntegrationTest {
    @Autowired
    LikeService likeService;
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MyRepository myRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    QuestionService questionService;
    static final String AUTHORIZED_USER = "999";

    static Long saveMemberId;

    static QuestionSaveDto firstQuestionSaveDto;
    static QuestionSaveDto secondQuestionSaveDto;
    static QuestionSaveDto thirdQuestionSaveDto;

    @BeforeEach
    void init() {
        saveUser("user");
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

    @Test
    @DisplayName("일반 질문 생성 후 다른 유저가 좋아요를 누를 경우")
    @Transactional
    void createQuestion(){
        Question question = makeQuestion();
        saveUserAndLike(question);


    }

    private void saveUserAndLike(Post post) {
        PostType postType = null;
        if (post instanceof Question){
            postType = PostType.QUESTION;
        }
        else if (post instanceof Answer){
            postType = PostType.ANSWER;
        }

        MemberSaveDto memberSaveDto = MemberSaveDto.builder()
                .userName("like_user")
                .loginType(LoginType.NON_SOCIAL)
                .userPw("a1234567!")
                .userEmail("test@gmail.com")
                .build();
        MemberProfileEntity memberProfileEntity = MemberProfileEntity.builder()
                .nickname("like_user")
                .profileImageFilePath("taehoon-image")
                .introduce("introduce of taehoon")
                .userTags(List.of("tag1", "tag2"))
                .build();
        SaveMemberResponseDto saveMemberResponseDto = memberRepository.save(NonSocialMember.createNonSocialMember(memberSaveDto));
        Long likeUserId = saveMemberResponseDto.getId();
        myRepository.createProfile(likeUserId, memberProfileEntity);

        likeService.likePost(postType,post.getId());
    }

    private Question makeQuestion() {
        QuestionSaveDto questionSaveDto = QuestionSaveDto.builder().title("테스트 제목 생성")
                .text("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 내용\", \"type\": \"text\"}]}]}")
                .tags(List.of("tag1", "tag2"))
                .build();

        return questionService.createQuestion(questionSaveDto, saveMemberId,Long.valueOf(Constant.NONE_MEMO_QUESTION ));
    }
}
