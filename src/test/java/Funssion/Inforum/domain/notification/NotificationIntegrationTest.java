package Funssion.Inforum.domain.notification;

import Funssion.Inforum.common.constant.NotificationType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.follow.repository.FollowRepository;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.Member;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.Constant;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.score.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static Funssion.Inforum.common.constant.NotificationType.*;
import static Funssion.Inforum.common.constant.PostType.*;
import static Funssion.Inforum.domain.score.Rank.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class NotificationIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemoRepository memoRepository;
    @Autowired
    FollowRepository followRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    CommentRepository commentRepository;

    SocialMember testUser1 = SocialMember.builder()
            .userName("jinu")
            .userEmail("jinu@gmail.com")
            .imagePath("https://image")
            .loginType(LoginType.SOCIAL)
            .build();
    SocialMember testUser2 = SocialMember.builder()
            .userName("jinu2")
            .userEmail("jinu2@gmail.com")
            .imagePath("https://image2")
            .loginType(LoginType.SOCIAL)
            .build();
    NonSocialMember testUser3 = NonSocialMember.builder()
            .userName("jinu3")
            .userEmail("jinu3@hanmail.com")
            .imagePath("https://image3")
            .loginType(LoginType.NON_SOCIAL)
            .userPw("jinu")
            .build();
    NonSocialMember testUser4 = NonSocialMember.builder()
            .userName("jinu4")
            .userEmail("jinu4@hanmail.com")
            .imagePath("https://image4")
            .loginType(LoginType.NON_SOCIAL)
            .userPw("jinu")
            .build();
    Long testUser1Id;
    Long testUser2Id;
    Long testUser3Id;
    Long testUser4Id;
    String testUser1Rank = INFINITY_5.toString();
    String testUser2Rank = BRONZE_1.toString();
    String testUser3Rank = GOLD_3.toString();
    String testUser4Rank = PLATINUM_1.toString();


    @BeforeEach
    void init() {
        testUser1Id = memberRepository.save(testUser1).getId();
        testUser2Id = memberRepository.save(testUser2).getId();
        testUser3Id = memberRepository.save(testUser3).getId();
        testUser4Id = memberRepository.save(testUser4).getId();
    }


    @Nested
    @DisplayName("댓글 알림")
    class newCommentNotificationTest {

        @Nested
        @DisplayName("메모에 새로운 댓글이 달린 경우")
        class newCommentInMemo {

            Memo createdMemo;

            String commentFormJson;

            @BeforeEach
            void init() {
                createdMemo = memoRepository.create(
                        Memo.builder()
                                .authorId(testUser1Id)
                                .authorName(testUser1.getUserName())
                                .authorImagePath(testUser1.getImagePath())
                                .rank(testUser1Rank)
                                .title("testMemo")
                                .description("test memo is ...")
                                .text("{\"content\": \"test memo is good\"}")
                                .color("yellow")
                                .rank(BRONZE_1.toString())
                                .isTemporary(false)
                                .memoTags(Collections.emptyList())
                                .build()
                );

                commentFormJson =
                        "{" +
                                "\"postTypeWithComment\": \"MEMO\", " +
                                "\"postId\": \""+createdMemo.getId()+"\", " +
                                "\"commentText\": \""+"memo is Good"+"\"" +
                        "}";
            }

            @Test
            @DisplayName("일반  케이스")
            void normalCase() throws Exception {

                mvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentFormJson)
                        .with(user(testUser2Id.toString())))
                        .andExpect(status().isCreated())
                        .andExpect(content().string(containsString("\"isSuccess\":true")));

                mvc.perform(get("/notifications")
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("\"message\":\""+ NEW_COMMENT.getMessage() +"\"")))
                        .andExpect(content().string(containsString("\"senderId\":"+ testUser2Id)))
                        .andExpect(content().string(containsString("\"postTypeToShow\":\""+ MEMO +"\"")))
                        .andExpect(content().string(containsString("\"postIdToShow\":"+ createdMemo.getId())));


                mvc.perform(get("/notifications")
                                .with(user(testUser2Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));
            }

            @Test
            @DisplayName("본인 메모에 댓글단 경우")
            void commentWithMyMemo() throws Exception {

                mvc.perform(post("/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(commentFormJson)
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isCreated())
                        .andExpect(content().string(containsString("\"isSuccess\":true")));

                mvc.perform(get("/notifications")
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));
            }
        }

        @Nested
        @DisplayName("질문에 새로운 댓글이 달린 경우")
        class newCommentInQuestion{

            Question newQuestion;
            String commentFormJson;

            @BeforeEach
            void init() {
                newQuestion = questionRepository.createQuestion(
                        Question.builder()
                                .authorId(testUser1Id)
                                .authorName(testUser1.getUserName())
                                .authorImagePath(testUser1.getImagePath())
                                .title("java")
                                .text("{\"content\":\"java is good?\"}")
                                .description("java is ...")
                                .tags(Collections.emptyList())
                                .memoId(Long.valueOf(Constant.NONE_MEMO_QUESTION))
                                .rank(testUser1Rank)
                                .build()
                );

                commentFormJson =
                        "{" +
                                "\"postTypeWithComment\": \"QUESTION\", " +
                                "\"postId\": \""+newQuestion.getId()+"\", " +
                                "\"commentText\": \""+"question is Good"+"\"" +
                        "}";
            }

            @Test
            @DisplayName("일반 케이스")
            void normalCase() throws Exception {
                mvc.perform(post("/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(commentFormJson)
                                .with(user(testUser2Id.toString())))
                        .andExpect(status().isCreated())
                        .andExpect(content().string(containsString("\"isSuccess\":true")));

                mvc.perform(get("/notifications")
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("\"message\":\""+ NEW_COMMENT.getMessage() +"\"")))
                        .andExpect(content().string(containsString("\"senderId\":"+ testUser2Id)))
                        .andExpect(content().string(containsString("\"postTypeToShow\":\""+ QUESTION +"\"")))
                        .andExpect(content().string(containsString("\"postIdToShow\":"+ newQuestion.getId())));


                mvc.perform(get("/notifications")
                                .with(user(testUser2Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));
            }

            @Test
            @DisplayName("본인 질문에 댓글단 경우")
            void commentWithMyQuestion() throws Exception {

                mvc.perform(post("/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(commentFormJson)
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isCreated())
                        .andExpect(content().string(containsString("\"isSuccess\":true")));

                mvc.perform(get("/notifications")
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));
            }
        }

        @Nested
        @DisplayName("답변에 새로운 댓글이 달린 경우")
        class newAnswerInQuestion{
            Question createdQuestion;
            Answer newAnswer;
            String commentFormJson;

            @BeforeEach
            void init() {
                createdQuestion = questionRepository.createQuestion(
                        Question.builder()
                                .authorId(testUser1Id)
                                .authorName(testUser1.getUserName())
                                .authorImagePath(testUser1.getImagePath())
                                .title("java")
                                .text("{\"content\":\"java is good?\"}")
                                .description("java is ...")
                                .tags(Collections.emptyList())
                                .memoId(Long.valueOf(Constant.NONE_MEMO_QUESTION))
                                .rank(testUser1Rank)
                                .build()
                );

                newAnswer = answerRepository.createAnswer(
                        Answer.builder()
                                .authorId(testUser1Id)
                                .authorName(testUser1.getUserName())
                                .authorImagePath(testUser1.getImagePath())
                                .text("{\"content\":\"java is good.\"}")
                                .questionId(createdQuestion.getId())
                                .rank(testUser1Rank)
                                .build()
                );

                commentFormJson =
                        "{" +
                                "\"postTypeWithComment\": \"ANSWER\", " +
                                "\"postId\": \""+newAnswer.getId()+"\", " +
                                "\"commentText\": \""+"answer is Good"+"\"" +
                        "}";
            }

            @Test
            @DisplayName("일반 케이스")
            void normalCase() throws Exception {
                mvc.perform(post("/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(commentFormJson)
                                .with(user(testUser2Id.toString())))
                        .andExpect(status().isCreated())
                        .andExpect(content().string(containsString("\"isSuccess\":true")));

                mvc.perform(get("/notifications")
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("\"message\":\""+ NEW_COMMENT.getMessage() +"\"")))
                        .andExpect(content().string(containsString("\"senderId\":"+ testUser2Id)))
                        .andExpect(content().string(containsString("\"postTypeToShow\":\""+ QUESTION +"\"")))
                        .andExpect(content().string(containsString("\"postIdToShow\":"+ createdQuestion.getId())));


                mvc.perform(get("/notifications")
                                .with(user(testUser2Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));
            }

            @Test
            @DisplayName("본인 답변에 댓글단 경우")
            void commentWithMyAnswer() throws Exception {

                mvc.perform(post("/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(commentFormJson)
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isCreated())
                        .andExpect(content().string(containsString("\"isSuccess\":true")));

                mvc.perform(get("/notifications")
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));
            }
        }
    }

    @Nested
    @DisplayName("대댓글 알림")
    class newRecommentNotificationTest {

        @Nested
        @DisplayName("메모 댓글에 대댓글이 달린 경우")
        class newRecommentInOnlyOneComment {

            Memo createdMemo;
            Comment createdComment;

            String recommentFormJson;

            @BeforeEach
            void init() {
                createdMemo = memoRepository.create(
                        Memo.builder()
                                .authorId(testUser1Id)
                                .authorName(testUser1.getUserName())
                                .authorImagePath(testUser1.getImagePath())
                                .rank(testUser1Rank)
                                .title("testMemo")
                                .description("test memo is ...")
                                .text("{\"content\": \"test memo is good\"}")
                                .color("yellow")
                                .isTemporary(false)
                                .memoTags(Collections.emptyList())
                                .build()
                );

                createdComment = commentRepository.createComment(
                        Comment.builder()
                                .authorId(testUser2Id)
                                .authorName(testUser2.getUserName())
                                .authorImagePath(testUser2.getImagePath())
                                .postTypeWithComment(MEMO)
                                .postId(createdMemo.getId())
                                .commentText("memo is bad")
                                .rank(testUser2Rank)
                                .build()
                );

                System.out.println("createdComment = " + createdComment);

                recommentFormJson =
                        "{" +
                                "\"parentCommentId\": "+createdComment.getId()+", " +
                                "\"commentText\": \""+"you too"+"\"" +
                        "}";
            }

            @Test
            @DisplayName("일반 케이스")
            void normalCase() throws Exception {

                mvc.perform(post("/comments/recomments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(recommentFormJson)
                                .with(user(testUser3Id.toString())))
                        .andExpect(status().isCreated())
                        .andExpect(content().string(containsString("\"isSuccess\":true")));

                mvc.perform(get("/notifications")
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));

                mvc.perform(get("/notifications")
                                .with(user(testUser2Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("\"message\":\""+ NEW_COMMENT.getMessage() +"\"")))
                        .andExpect(content().string(containsString("\"senderId\":"+ testUser3Id)))
                        .andExpect(content().string(containsString("\"postTypeToShow\":\""+ MEMO +"\"")))
                        .andExpect(content().string(containsString("\"postIdToShow\":"+ createdMemo.getId())));


                mvc.perform(get("/notifications")
                                .with(user(testUser3Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));
            }

            @Test
            @DisplayName("본인 댓글에 대댓글단 경우")
            void recommentWithMyComment() throws Exception {

                mvc.perform(post("/comments/recomments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(recommentFormJson)
                                .with(user(testUser2Id.toString())))
                        .andExpect(status().isCreated())
                        .andExpect(content().string(containsString("\"isSuccess\":true")));

                mvc.perform(get("/notifications")
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));

                mvc.perform(get("/notifications")
                                .with(user(testUser2Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));


                mvc.perform(get("/notifications")
                                .with(user(testUser3Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));
            }

//            @Test
//            @DisplayName("대댓글이 이미 존재하는 댓글에 대댓글을 다는 경우")
//            void recommentWithCommentAlreadyHasRecomment
        }
    }


}
