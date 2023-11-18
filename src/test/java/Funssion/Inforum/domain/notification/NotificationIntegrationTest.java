package Funssion.Inforum.domain.notification;

import Funssion.Inforum.domain.follow.repository.FollowRepository;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

import static Funssion.Inforum.common.constant.NotificationType.*;
import static Funssion.Inforum.common.constant.OrderType.NEW;
import static Funssion.Inforum.common.constant.PostType.*;
import static Funssion.Inforum.domain.post.qna.Constant.*;
import static Funssion.Inforum.domain.score.Rank.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        @DisplayName("게시물에 새로운 댓글이 달린 경우")
        class newCommentInMemo {

            Memo createdMemo;
            Question createdQuestion;
            Answer createdAnswer;

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

                createdQuestion = questionRepository.createQuestion(
                        Question.builder()
                                .authorId(testUser1Id)
                                .authorName(testUser1.getUserName())
                                .authorImagePath(testUser1.getImagePath())
                                .title("java")
                                .text("{\"content\":\"java is good?\"}")
                                .description("java is ...")
                                .tags(Collections.emptyList())
                                .memoId(Long.valueOf(NONE_MEMO_QUESTION))
                                .rank(testUser1Rank)
                                .build()
                );

                createdAnswer = answerRepository.createAnswer(
                        Answer.builder()
                                .authorId(testUser1Id)
                                .authorName(testUser1.getUserName())
                                .authorImagePath(testUser1.getImagePath())
                                .text("{\"content\":\"java is good.\"}")
                                .questionId(createdQuestion.getId())
                                .rank(testUser1Rank)
                                .build()
                );
            }

            @Test
            @DisplayName("일반 케이스")
            void normalCase() throws Exception {
                String commentFormJson =
                        "{" +
                                "\"postTypeWithComment\": \"MEMO\", " +
                                "\"postId\": \""+createdMemo.getId()+"\", " +
                                "\"commentText\": \""+"memo is Good"+"\"" +
                        "}";

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
            @DisplayName("본인 질문에 댓글단 경우")
            void commentWithMyQuestion() throws Exception {

                String commentFormJson =
                        "{" +
                                "\"postTypeWithComment\": \"QUESTION\", " +
                                "\"postId\": \""+createdQuestion.getId()+"\", " +
                                "\"commentText\": \""+"question is Good"+"\"" +
                        "}";

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

            @Test
            @DisplayName("답변에 댓글 달았다가 삭제하는 경우")
            void commentWithAnswerAndDelete() throws Exception {
                //create
                String commentFormJson =
                        "{" +
                                "\"postTypeWithComment\": \"ANSWER\", " +
                                "\"postId\": \""+createdAnswer.getId()+"\", " +
                                "\"commentText\": \""+"answer is Good"+"\"" +
                        "}";

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

                CommentListDto savedComment = commentRepository.getCommentsAtPost(ANSWER, createdAnswer.getId(), testUser2Id).get(0);

                //delete
                mvc.perform(delete("/comments/"+savedComment.getId())
                        .with(user(testUser2Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("\"isSuccess\":true")));

                mvc.perform(get("/notifications")
                                .with(user(testUser1Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));


                mvc.perform(get("/notifications")
                                .with(user(testUser2Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));
            }
        }
    }

    @Nested
    @DisplayName("대댓글 알림")
    class newRecommentNotificationTest {

        @Nested
        @DisplayName("메모, 질문, 답변 댓글에 대댓글이 달린 경우")
        class newRecommentInOnlyOneComment {

            Memo createdMemo;
            Question createdQuestion;
            Answer createdAnswer;
            Comment createdCommentInMemo;
            Comment createdCommentInQuestion;
            Comment createdCommentInAnswer;

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

                createdQuestion = questionRepository.createQuestion(
                        Question.builder()
                                .authorId(testUser1Id)
                                .authorName(testUser1.getUserName())
                                .authorImagePath(testUser1.getImagePath())
                                .title("java")
                                .text("{\"content\":\"java is good?\"}")
                                .description("java is ...")
                                .tags(Collections.emptyList())
                                .memoId(Long.valueOf(NONE_MEMO_QUESTION))
                                .rank(testUser1Rank)
                                .build()
                );

                createdAnswer = answerRepository.createAnswer(
                        Answer.builder()
                                .authorId(testUser1Id)
                                .authorName(testUser1.getUserName())
                                .authorImagePath(testUser1.getImagePath())
                                .text("{\"content\":\"java is good.\"}")
                                .questionId(createdQuestion.getId())
                                .rank(testUser1Rank)
                                .build()
                );

                createdCommentInMemo = commentRepository.createComment(
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

                createdCommentInQuestion = commentRepository.createComment(
                        Comment.builder()
                                .authorId(testUser2Id)
                                .authorName(testUser2.getUserName())
                                .authorImagePath(testUser2.getImagePath())
                                .postTypeWithComment(QUESTION)
                                .postId(createdQuestion.getId())
                                .commentText("memo is bad")
                                .rank(testUser2Rank)
                                .build()
                );

                createdCommentInAnswer = commentRepository.createComment(
                        Comment.builder()
                                .authorId(testUser2Id)
                                .authorName(testUser2.getUserName())
                                .authorImagePath(testUser2.getImagePath())
                                .postTypeWithComment(ANSWER)
                                .postId(createdAnswer.getId())
                                .commentText("memo is bad")
                                .rank(testUser2Rank)
                                .build()
                );
            }

            @Test
            @DisplayName("메모 댓글에 대댓글을 달고 삭제하는 케이스")
            void normalCase() throws Exception {
                String recommentFormJson =
                        "{" +
                                "\"parentCommentId\": "+createdCommentInMemo.getId()+", " +
                                "\"commentText\": \""+"you too"+"\"" +
                        "}";

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

                ReCommentListDto savedRecomment = commentRepository.getReCommentsAtComment(createdCommentInMemo.getId(), testUser3Id).get(0);
                assertThat(savedRecomment.getCommentText()).isEqualTo("you too");

                mvc.perform(delete("/comments/recomments/"+savedRecomment.getId())
                        .with(user(testUser3Id.toString())))
                        .andExpect(status().isOk())
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

            @Test
            @DisplayName("질문에 있는 본인 댓글에 대댓글단 경우")
            void recommentWithMyComment() throws Exception {
                String recommentFormJson =
                        "{" +
                                "\"parentCommentId\": "+createdCommentInQuestion.getId()+", " +
                                "\"commentText\": \""+"you too"+"\"" +
                        "}";

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

            @Test
            @DisplayName("대댓글이 이미 존재하는 답변 댓글에 대댓글을 달고 삭제하는 경우")
            void recommentWithCommentAlreadyHasRecomment() throws Exception {
                String recommentFormJson =
                        "{" +
                                "\"parentCommentId\": "+createdCommentInAnswer.getId()+", " +
                                "\"commentText\": \"you too2\"" +
                        "}";

                // given 2번 댓글, 3번 대댓글
                commentRepository.createReComment(
                        ReComment.builder()
                                .authorId(testUser3Id)
                                .authorName(testUser3.getUserName())
                                .authorImagePath(testUser3.getImagePath())
                                .commentText("yoo too")
                                .parentCommentId(createdCommentInAnswer.getId())
                                .rank(testUser3Rank)
                                .createdDate(LocalDateTime.now())
                                .build()
                );

                mvc.perform(post("/comments/recomments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(recommentFormJson)
                                .with(user(testUser4Id.toString())))
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
                        .andExpect(content().string(containsString("\"senderId\":"+ testUser4Id)))
                        .andExpect(content().string(containsString("\"postTypeToShow\":\""+ QUESTION +"\"")))
                        .andExpect(content().string(containsString("\"postIdToShow\":"+ createdQuestion.getId())));


                mvc.perform(get("/notifications")
                                .with(user(testUser3Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("\"message\":\""+ NEW_COMMENT.getMessage() +"\"")))
                        .andExpect(content().string(containsString("\"senderId\":"+ testUser4Id)))
                        .andExpect(content().string(containsString("\"postTypeToShow\":\""+ QUESTION +"\"")))
                        .andExpect(content().string(containsString("\"postIdToShow\":"+ createdQuestion.getId())));

                mvc.perform(get("/notifications")
                                .with(user(testUser4Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));

                ReCommentListDto savedRecomment = commentRepository.getReCommentsAtComment(createdCommentInAnswer.getId(), testUser4Id).get(1);
                assertThat(savedRecomment.getCommentText()).isEqualTo("you too2");

                mvc.perform(delete("/comments/recomments/"+savedRecomment.getId())
                        .with(user(testUser4Id.toString())))
                        .andExpect(status().isOk())
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

                mvc.perform(get("/notifications")
                                .with(user(testUser4Id.toString())))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[]"));
            }
        }
    }

    @Nested
    @DisplayName("답변 알림")
    class newAnswerNotification {

        Question createdQuestion;

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
                            .memoId(Long.valueOf(NONE_MEMO_QUESTION))
                            .rank(testUser1Rank)
                            .build()
            );
        }

        @Test
        @DisplayName("질문에 답변을 달고 삭제하는 경우")
        void newAnswerInQuestion() throws Exception {
            String answerSaveForm =
                    "{" +
                            "\"text\": \"{\\\"content\\\": \\\"good\\\"}\"" +
                    "}";

            mvc.perform(post("/answers")
                    .content(answerSaveForm)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("questionId", createdQuestion.getId().toString())
                    .with(user(testUser2Id.toString())))
                    .andExpect(status().isCreated())
                    .andExpect(content().string(containsString("\"isSuccess\":true")));

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_ANSWER.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser2Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":\""+ QUESTION +"\"")))
                    .andExpect(content().string(containsString("\"postIdToShow\":"+ createdQuestion.getId())));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));

            Answer savedAnswer = answerRepository.getAnswersOfQuestion(testUser2Id, createdQuestion.getId()).get(0);
            assertThat(savedAnswer.getAuthorId()).isEqualTo(testUser2Id);

            mvc.perform(delete("/answers/"+savedAnswer.getId())
                    .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"isSuccess\":true")));

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));
        }

    }

    @Nested
    @DisplayName("질문 알림")
    class newQuestionNotification {

        Memo createdMemo;
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
        }

        @Test
        @DisplayName("메모에 질문을 달고 다시 삭제하는 경우")
        void newQuestionInMemo() throws Exception {
            String questionSaveForm =
                    "{" +
                            "\"title\": \"java\"," +
                            "\"text\": \"{\\\"content\\\": \\\"java is good?\\\"}\"," +
                            "\"description\": \"java is ...\"," +
                            "\"tags\": [\"java\"]" +
                    "}";

            mvc.perform(post("/questions")
                            .content(questionSaveForm)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("memoId", createdMemo.getId().toString())
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isCreated());

            Question savedQuestion = questionRepository.getQuestionsOfMemo(testUser2Id, createdMemo.getId()).get(0);
            assertThat(savedQuestion.getMemoId()).isEqualTo(createdMemo.getId());
            assertThat(savedQuestion.getAuthorId()).isEqualTo(testUser2Id);

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_QUESTION.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser2Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":\""+ QUESTION +"\"")))
                    .andExpect(content().string(containsString("\"postIdToShow\":"+ savedQuestion.getId())));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));


            mvc.perform(delete("/questions/"+savedQuestion.getId())
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"isSuccess\":true")));

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));
        }
    }

    @Nested
    @DisplayName("팔로우 알림")
    class newFollowNotification {

        @Test
        @DisplayName("유저 1 이 유저 2 를 팔로우 했다가 취소")
        void followOneAndUnfollowOne() throws Exception {
            mvc.perform(post("/follow")
                    .with(user(testUser1Id.toString()))
                    .param("userId", testUser2Id.toString()))
                    .andExpect(status().isOk());

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_FOLLOWER.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser1Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":null")))
                    .andExpect(content().string(containsString("\"postIdToShow\":null")));

            mvc.perform(post("/unfollow")
                            .with(user(testUser1Id.toString()))
                            .param("userId", testUser2Id.toString()))
                    .andExpect(status().isOk());

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));
        }

        @Test
        @DisplayName("여러 유저가 유저 1 을 팔로우")
        void manyUserFollowOne() throws Exception {
            mvc.perform(post("/follow")
                            .with(user(testUser2Id.toString()))
                            .param("userId", testUser1Id.toString()))
                    .andExpect(status().isOk());

            mvc.perform(post("/follow")
                            .with(user(testUser3Id.toString()))
                            .param("userId", testUser1Id.toString()))
                    .andExpect(status().isOk());

            mvc.perform(post("/follow")
                            .with(user(testUser4Id.toString()))
                            .param("userId", testUser1Id.toString()))
                    .andExpect(status().isOk());

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_FOLLOWER.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser2Id)))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser3Id)))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser4Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":null")))
                    .andExpect(content().string(containsString("\"postIdToShow\":null")));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));

            mvc.perform(get("/notifications")
                            .with(user(testUser3Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));

            mvc.perform(get("/notifications")
                            .with(user(testUser4Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));
        }
    }

    @Nested
    @DisplayName("팔로잉 유저의 새로운 게시물 알림")
    class newPostFollowedNotification {

        @Test
        @DisplayName("팔로우한 유저가 새 메모를 게시했다가 삭제")
        void followingUserWriteNewMemo() throws Exception {
            mvc.perform(post("/follow")
                            .with(user(testUser1Id.toString()))
                            .param("userId", testUser2Id.toString()))
                    .andExpect(status().isOk());

            String memoSaveForm =
                    "{" +
                            "\"memoTitle\": \"java\"," +
                            "\"memoText\": \"{\\\"content\\\": \\\"java is good?\\\"}\"," +
                            "\"memoDescription\": \"java is ...\"," +
                            "\"memoTags\": [\"java\"]," +
                            "\"memoColor\": \"yellow\"" +
                    "}";

            mvc.perform(post("/memos")
                    .with(user(testUser2Id.toString()))
                    .content(memoSaveForm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());

            Memo savedMemo = memoRepository.findAllByUserIdOrderById(testUser2Id, DEFAULT_PAGE_NUM, DEFAULT_RESULT_SIZE_PER_PAGE).get(0);
            assertThat(savedMemo.getTitle()).isEqualTo("java");
            assertThat(savedMemo.getAuthorId()).isEqualTo(testUser2Id);

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_POST_FOLLOWED.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser2Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":\"MEMO\"")))
                    .andExpect(content().string(containsString("\"postIdToShow\":"+ savedMemo.getId())));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_FOLLOWER.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser1Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":null")))
                    .andExpect(content().string(containsString("\"postIdToShow\":null")));

            mvc.perform(delete("/memos/"+savedMemo.getId())
                    .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk());

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_FOLLOWER.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser1Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":null")))
                    .andExpect(content().string(containsString("\"postIdToShow\":null")));
        }

        @Test
        @DisplayName("여러 유저가 팔로우 한 유저가 질문 작성 후 삭제")
        void manyFollowingUserWriteNewQuestion() throws Exception {
            mvc.perform(post("/follow")
                            .with(user(testUser2Id.toString()))
                            .param("userId", testUser1Id.toString()))
                    .andExpect(status().isOk());

            mvc.perform(post("/follow")
                            .with(user(testUser3Id.toString()))
                            .param("userId", testUser1Id.toString()))
                    .andExpect(status().isOk());

            mvc.perform(post("/follow")
                            .with(user(testUser4Id.toString()))
                            .param("userId", testUser1Id.toString()))
                    .andExpect(status().isOk());

            String questionSaveForm =
                    "{" +
                            "\"title\": \"java\"," +
                            "\"text\": \"{\\\"content\\\": \\\"java is good?\\\"}\"," +
                            "\"description\": \"java is ...\"," +
                            "\"tags\": [\"java\"]" +
                    "}";

            mvc.perform(post("/questions")
                            .content(questionSaveForm)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isCreated());

            Question savedQuestion = questionRepository.getMyQuestions(testUser1Id, NEW, DEFAULT_PAGE_NUM, DEFAULT_RESULT_SIZE_PER_PAGE).get(0);
            assertThat(savedQuestion.getMemoId()).isEqualTo(Long.valueOf(NONE_MEMO_QUESTION));
            assertThat(savedQuestion.getAuthorId()).isEqualTo(testUser1Id);

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_FOLLOWER.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser2Id)))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser3Id)))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser4Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":null")))
                    .andExpect(content().string(containsString("\"postIdToShow\":null")));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_POST_FOLLOWED.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser1Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":\"QUESTION\"")))
                    .andExpect(content().string(containsString("\"postIdToShow\":"+ savedQuestion.getId())));

            mvc.perform(get("/notifications")
                            .with(user(testUser3Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_POST_FOLLOWED.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser1Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":\"QUESTION\"")))
                    .andExpect(content().string(containsString("\"postIdToShow\":"+ savedQuestion.getId())));

            mvc.perform(get("/notifications")
                            .with(user(testUser4Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_POST_FOLLOWED.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser1Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":\"QUESTION\"")))
                    .andExpect(content().string(containsString("\"postIdToShow\":"+ savedQuestion.getId())));

            mvc.perform(delete("/questions/"+savedQuestion.getId())
                    .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"isSuccess\":true")));

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_FOLLOWER.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser2Id)))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser3Id)))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser4Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":null")))
                    .andExpect(content().string(containsString("\"postIdToShow\":null")));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));

            mvc.perform(get("/notifications")
                            .with(user(testUser3Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));

            mvc.perform(get("/notifications")
                            .with(user(testUser4Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));
        }

        @Test
        @DisplayName("내가 팔로우한 유저가 내 메모에 질문 작성")
        void followingUserWriteNewQuestionInMyMemo() throws Exception {
            Memo createdMemo = memoRepository.create(
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

            mvc.perform(post("/follow")
                            .with(user(testUser1Id.toString()))
                            .param("userId", testUser2Id.toString()))
                    .andExpect(status().isOk());

            String questionSaveForm =
                    "{" +
                            "\"title\": \"java\"," +
                            "\"text\": \"{\\\"content\\\": \\\"java is good?\\\"}\"," +
                            "\"description\": \"java is ...\"," +
                            "\"tags\": [\"java\"]" +
                    "}";

            mvc.perform(post("/questions")
                            .content(questionSaveForm)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("memoId", createdMemo.getId().toString())
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isCreated());

            Question savedQuestion = questionRepository.getMyQuestions(testUser2Id, NEW, DEFAULT_PAGE_NUM, DEFAULT_RESULT_SIZE_PER_PAGE).get(0);
            assertThat(savedQuestion.getMemoId()).isEqualTo(createdMemo.getId());
            assertThat(savedQuestion.getAuthorId()).isEqualTo(testUser2Id);

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_QUESTION.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser2Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":\"QUESTION\"")))
                    .andExpect(content().string(containsString("\"postIdToShow\":"+savedQuestion.getId())))
                    .andExpect(content().string(Matchers.not(containsString("\"message\":\""+ NEW_POST_FOLLOWED.getMessage() +"\""))));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_FOLLOWER.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser1Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":null")))
                    .andExpect(content().string(containsString("\"postIdToShow\":null")));
        }
    }

    @Nested
    @DisplayName("답변 채택 알림")
    class newSelectedNotification {

        Question createdQuestion;
        Answer createdAnswer;
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
                            .memoId(Long.valueOf(NONE_MEMO_QUESTION))
                            .rank(testUser1Rank)
                            .build()
            );

            createdAnswer = answerRepository.createAnswer(
                    Answer.builder()
                            .authorId(testUser2Id)
                            .authorName(testUser2.getUserName())
                            .authorImagePath(testUser2.getImagePath())
                            .text("{\"content\":\"java is good.\"}")
                            .questionId(createdQuestion.getId())
                            .rank(testUser1Rank)
                            .build()
            );
        }

        @Test
        @DisplayName("일반 케이스")
        void normalCase() throws Exception {
            mvc.perform(patch("/answers/select/"+createdQuestion.getId())
                    .param("answerId", createdAnswer.getId().toString())
                    .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"isSuccess\":true")));

            mvc.perform(get("/notifications")
                            .with(user(testUser1Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string("[]"));

            mvc.perform(get("/notifications")
                            .with(user(testUser2Id.toString())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"message\":\""+ NEW_ACCEPTED.getMessage() +"\"")))
                    .andExpect(content().string(containsString("\"senderId\":"+ testUser1Id)))
                    .andExpect(content().string(containsString("\"postTypeToShow\":\"QUESTION\"")))
                    .andExpect(content().string(containsString("\"postIdToShow\":"+createdQuestion.getId())));
        }
    }

}
