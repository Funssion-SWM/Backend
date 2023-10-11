package Funssion.Inforum.domain.member;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.Member;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.Constant;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MemberIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MyRepository myRepository;

    @Autowired
    MemoRepository memoRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    CommentRepository commentRepository;

    Long savedNonsocialMemberId;
    Long savedSocialMemberId;
    String savedNonsocialMemberEmail = "nonsocial@gmail.com";
    String savedSocialMemberEmail = "social@gmail.com";
    String savedMemberName = "jinu";
    String savedMemberImagePath = "https://image";
    SaveMemberResponseDto savedNonsocialMember;
    SaveMemberResponseDto savedSocialMember;
    Memo savedMemo;
    Question savedQuestion;
    Answer savedAnswer;
    Comment savedComment;
    ReCommentListDto savedReComment;

    @BeforeEach
    void init() {
        savedNonsocialMember = memberRepository.save(NonSocialMember.builder()
                .userPw("1234")
                .userEmail(savedNonsocialMemberEmail)
                .loginType(LoginType.NON_SOCIAL)
                .authId(1L)
                .userName(savedMemberName)
                .introduce("hi")
                .createdDate(LocalDateTime.now())
                .tags("Java")
                .imagePath(savedMemberImagePath)
                .build());

        savedNonsocialMemberId = savedNonsocialMember.getId();

        savedSocialMember = memberRepository.save(NonSocialMember.builder()
                .userPw("1234")
                .userEmail(savedSocialMemberEmail)
                .loginType(LoginType.SOCIAL)
                .authId(1L)
                .userName(savedMemberName)
                .introduce("hi")
                .createdDate(LocalDateTime.now())
                .tags("Java")
                .imagePath(savedMemberImagePath)
                .build());

        savedSocialMemberId = savedSocialMember.getId();

        savedMemo = memoRepository.create(Memo.builder()
                .title("JPA")
                .text("{\"content\" : \"JPA is JPA\"}")
                .description("JPA is ...")
                .color("yellow")
                .authorId(savedNonsocialMemberId)
                .authorName(savedMemberName)
                .authorImagePath(savedMemberImagePath)
                .createdDate(LocalDateTime.now().minusDays(1))
                .likes(0L)
                .isTemporary(false)
                .memoTags(List.of("Java", "JPA"))
                .build());

        savedQuestion = questionRepository.createQuestion(Question.builder()
                .authorId(savedNonsocialMemberId)
                .authorName(savedMemberName)
                .authorImagePath(savedMemberImagePath)
                .title("JPA")
                .text("{\"content\" : \"JPA is what?\"}")
                .tags(List.of("JAVA"))
                .memoId(savedMemo.getId())
                .description("JPA ...")
                .build());

        savedAnswer = answerRepository.createAnswer(Answer.builder()
                .questionId(savedQuestion.getId())
                .authorId(savedNonsocialMemberId)
                .authorName(savedMemberName)
                .authorImagePath(savedMemberImagePath)
                .text("{\"content\" : \"JPA is good.\"}")
                .build());

        savedComment = commentRepository.createComment(Comment.builder()
                .authorId(savedNonsocialMemberId)
                .authorName(savedMemberName)
                .authorImagePath(savedMemberImagePath)
                .postTypeWithComment(PostType.MEMO)
                .postId(savedMemo.getId())
                .commentText("wow")
                .build());

        commentRepository.createReComment(ReComment.builder()
                .authorId(savedNonsocialMemberId)
                .authorName(savedMemberName)
                .authorImagePath(savedMemberImagePath)
                .parentCommentId(savedComment.getId())
                .commentText("wow")
                .createdDate(LocalDateTime.now())
                .build());

        savedReComment = commentRepository.getReCommentsAtComment(savedComment.getId(), savedNonsocialMemberId).get(0);
    }

    @Nested
    @DisplayName("회원 탈퇴하기")
    class withdrawMember {

        @Test
        @DisplayName("로그인 없이 회원 탈퇴 시도")
        void withdrawWithOutLogin() throws Exception {

            mvc.perform(post("/users/withdraw")
                            .with(user(SecurityContextUtils.ANONYMOUS_USER_NAME)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("자체 회원가입 유저 정상 회원 탍퇴")
        void success() throws Exception {

            mvc.perform(post("/users/withdraw")
                            .with(user(savedNonsocialMemberId.toString())))
                    .andExpect(status().isOk());

            String deletedUserName = memberRepository.findNameById(savedNonsocialMemberId);
            MemberProfileEntity deletedUserProfile = myRepository.findProfileByUserId(savedNonsocialMemberId);
            Memo memoByDeletedUser = memoRepository.findById(savedMemo.getId());
            Question questionByDeletedUser = questionRepository.getOneQuestion(savedQuestion.getId());
            Answer answerByDeletedUser = answerRepository.getAnswerById(savedAnswer.getId());
            CommentListDto commentByDeletedUser = commentRepository.getCommentsAtPost(PostType.MEMO, savedMemo.getId(), deletedUserProfile.getUserId()).get(0);
            ReCommentListDto reCommentByDeletedUser = commentRepository.getReCommentsAtComment(commentByDeletedUser.getId(), deletedUserProfile.getUserId()).get(0);

            assertThat(deletedUserName).hasSize(15);

            assertThat(deletedUserProfile.getNickname()).isEqualTo(deletedUserName);
            assertThat(deletedUserProfile.getProfileImageFilePath()).isNull();
            assertThat(deletedUserProfile.getIntroduce()).isEqualTo("탈퇴한 유저입니다.");

            assertThat(memberRepository.findByName(deletedUserName)).isNotPresent();
            assertThat(memberRepository.findNonSocialMemberByEmail(savedNonsocialMemberEmail)).isNotPresent();
            assertThatThrownBy(() -> memberRepository.findEmailByNickname(deletedUserName))
                    .isInstanceOf(NotFoundException.class);

            assertThat(memoByDeletedUser.getAuthorImagePath()).isNull();
            assertThat(memoByDeletedUser.getAuthorName()).isEqualTo(deletedUserName);

            assertThat(questionByDeletedUser.getAuthorImagePath()).isNull();
            assertThat(questionByDeletedUser.getAuthorName()).isEqualTo(deletedUserName);

            assertThat(answerByDeletedUser.getAuthorImagePath()).isNull();
            assertThat(answerByDeletedUser.getAuthorName()).isEqualTo(deletedUserName);

            assertThat(commentByDeletedUser.getAuthorImagePath()).isNull();
            assertThat(commentByDeletedUser.getAuthorName()).isEqualTo(deletedUserName);

            assertThat(reCommentByDeletedUser.getAuthorImagePath()).isNull();
            assertThat(reCommentByDeletedUser.getAuthorName()).isEqualTo(deletedUserName);
        }
        
        @Test
        @DisplayName("구글 로그인 회원가입 회원 정상 탈퇴") 
        void successWithGoogleAuth() throws Exception {

            mvc.perform(post("/users/withdraw")
                            .with(user(savedSocialMemberId.toString())))
                    .andExpect(status().isOk());

            String deletedUserName = memberRepository.findNameById(savedSocialMemberId);
            MemberProfileEntity deletedUserProfile = myRepository.findProfileByUserId(savedSocialMemberId);

            assertThat(deletedUserName).hasSize(15);

            assertThat(deletedUserProfile.getNickname()).isEqualTo(deletedUserName);
            assertThat(deletedUserProfile.getProfileImageFilePath()).isNull();
            assertThat(deletedUserProfile.getIntroduce()).isEqualTo("탈퇴한 유저입니다.");

            assertThat(memberRepository.findByName(deletedUserName)).isNotPresent();
            assertThat(memberRepository.findSocialMemberByEmail(savedSocialMemberEmail)).isNotPresent();
            assertThatThrownBy(() -> memberRepository.findEmailByNickname(deletedUserName))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}