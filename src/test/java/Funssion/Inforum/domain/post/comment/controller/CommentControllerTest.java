package Funssion.Inforum.domain.post.comment.controller;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.comment.service.CommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    CommentService commentService;

    static final String AUTHORIZED_USER = "1";
    static final Long MEMO_ID = 1L;
    static final Long QUESTION_ID = 1L;
    static final Long ANSWER_ID = 1L;

    static final Long COMMENT_ID = 1L;
    static final Long RE_COMMENT_ID = 1L;
    static String memoCommentSaveDto;
    static String memoCommentUpdateDto;
    static String questionCommentSaveDto;
    static String questionCommentUpdateDto;
    static String answerCommentSaveDto;
    static String answerCommentUpdateDto;
    static String reCommentSaveDto;
    static String reCommentUpdateDto;

    @BeforeAll
    static void init() throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        CommentSaveDto commentSaveDtoOfMemo = CommentSaveDto.builder()
                .postId(MEMO_ID)
                .postTypeWithComment(PostType.MEMO)
                .commentText("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"메모 댓글 내용\", \"type\": \"text\"}]}]}")
                .build();
        CommentUpdateDto commentUpdateDtoOfMemo = new CommentUpdateDto("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"메모 댓글 수정 내용\", \"type\": \"text\"}]}]}");

        CommentSaveDto commentSaveDtoOfQuestion = CommentSaveDto.builder()
                .postId(MEMO_ID)
                .postTypeWithComment(PostType.QUESTION)
                .commentText("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 댓글 내용\", \"type\": \"text\"}]}]}")
                .build();
        CommentUpdateDto commentUpdateDtoOfQuestion = new CommentUpdateDto("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"질문 댓글 수정 내용\", \"type\": \"text\"}]}]}");

        CommentSaveDto commentSaveDtoOfAnswer = CommentSaveDto.builder()
                .postId(MEMO_ID)
                .postTypeWithComment(PostType.ANSWER)
                .commentText("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 댓글 내용\", \"type\": \"text\"}]}]}")
                .build();
        CommentUpdateDto commentUpdateDtoOfAnswer = new CommentUpdateDto("{\"type\": \"doc\", \"content\": [{\"type\": \"paragraph\", \"content\": [{\"text\": \"답변 댓글 내용\", \"type\": \"text\"}]}]}");

        ReCommentSaveDto reCommentSaveRequestDto = new ReCommentSaveDto(COMMENT_ID,"대 댓글 내용입니다.");
        ReCommentUpdateDto reCommentUpdateRequestDto = new ReCommentUpdateDto("대댓글 수정 내용입니다.");

        memoCommentSaveDto = objectMapper.writeValueAsString(commentSaveDtoOfMemo);
        questionCommentSaveDto = objectMapper.writeValueAsString(commentSaveDtoOfQuestion);
        answerCommentSaveDto = objectMapper.writeValueAsString(commentSaveDtoOfAnswer);

        memoCommentUpdateDto = objectMapper.writeValueAsString(commentUpdateDtoOfMemo);
        questionCommentUpdateDto = objectMapper.writeValueAsString(commentUpdateDtoOfQuestion);
        answerCommentUpdateDto = objectMapper.writeValueAsString(commentUpdateDtoOfAnswer);

        reCommentSaveDto = objectMapper.writeValueAsString(reCommentSaveRequestDto);
        reCommentUpdateDto = objectMapper.writeValueAsString(reCommentUpdateRequestDto);
    }

    @Nested
    @DisplayName("댓글 작성")
    @WithMockUser(username=AUTHORIZED_USER)
    class createComment{
        @Test
        @DisplayName("포스트 타입별 댓글 작성")
        void createCommentOfQuestion() throws Exception {
            mvc.perform(post("/comments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(questionCommentSaveDto))
                    .andExpect(status().isCreated());
            mvc.perform(post("/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(memoCommentSaveDto))
                    .andExpect(status().isCreated());
            mvc.perform(post("/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(answerCommentSaveDto))
                    .andExpect(status().isCreated());
        }
    }
    @Nested
    @DisplayName("대댓글 작성")
    class createReComments{
        @Test
        @WithMockUser(username=AUTHORIZED_USER)
        @DisplayName("대댓글 작성")
        void createReComments() throws Exception {
            mvc.perform(post("/comments/recomments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reCommentSaveDto))
                    .andExpect(status().isCreated());
        }
    }


    @Nested
    @DisplayName("댓글 수정")
    class updateComment{
        @Test
        @WithMockUser(username=AUTHORIZED_USER)
        @DisplayName("댓글 작성자가 댓글을 수정")
        void updateCommentAuthorized() throws Exception {
            Long commentId = 9L;
            when(commentService.getAuthorIdOfComment(commentId,false))
                    .thenReturn(Long.valueOf(AUTHORIZED_USER));
            mvc.perform(patch("/comments/"+commentId)
                    .with(csrf())
                    .param("commentId",String.valueOf(commentId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(memoCommentUpdateDto))
                    .andExpect(status().isOk());
        }
        @Test
        @WithMockUser(username=AUTHORIZED_USER)
        @DisplayName("댓글 작성자가 아닌 사람이 해당 댓글을 수정")
        void updateCommentUnAuthorized() throws Exception {
            Long commentId = 9L;
            Long ownerId = 999L;
            when(commentService.getAuthorIdOfComment(commentId,false))
                    .thenReturn(Long.valueOf(ownerId));
            mvc.perform(patch("/comments/"+commentId)
                            .with(csrf())
                            .param("commentId",String.valueOf(commentId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(memoCommentUpdateDto))
                    .andExpect(status().isUnauthorized());
        }
    }
    @Nested
    @DisplayName("대 댓글 수정")
    class updateReComments{
        @Test
        @WithMockUser(username=AUTHORIZED_USER)
        @DisplayName("작성자가 대댓글을 수정하는 경우")
        void updateReCommentsAuthorized() throws Exception {
            when(commentService.getAuthorIdOfComment(RE_COMMENT_ID,true))
                    .thenReturn(Long.valueOf(AUTHORIZED_USER));

            mvc.perform(patch("/comments/recomments/"+RE_COMMENT_ID)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reCommentUpdateDto))
                    .andExpect(status().isOk());

        }
        @Test
        @WithMockUser(username=AUTHORIZED_USER)
        @DisplayName("작성자가 대댓글을 수정하는 경우")
        void updateReCommentsUnAuthorized() throws Exception {
            Long ownerId = 999L;

            when(commentService.getAuthorIdOfComment(RE_COMMENT_ID,true))
                    .thenReturn(Long.valueOf(ownerId));

            mvc.perform(patch("/comments/recomments/"+RE_COMMENT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(reCommentUpdateDto))
                    .andExpect(status().isUnauthorized());

        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class deleteComment{
        @Test
        @WithMockUser(username=AUTHORIZED_USER)
        @DisplayName("댓글 작성자가 댓글을 삭제")
        void deleteCommentAuthorized() throws Exception {
            when(commentService.getAuthorIdOfComment(COMMENT_ID,false)).thenReturn(Long.valueOf(AUTHORIZED_USER));
            mvc.perform(delete("/comments/"+COMMENT_ID)
                    .with(csrf()))
                    .andExpect(status().isOk());
        }
        @Test
        @WithMockUser(username=AUTHORIZED_USER)
        @DisplayName("댓글 작성자가 아닌 사람이 댓글을 삭제")
        void deleteCommentUnAuthorized() throws Exception {
            Long ownerId = 999L;
            when(commentService.getAuthorIdOfComment(COMMENT_ID,false)).thenReturn(ownerId);
            mvc.perform(delete("/comments/"+COMMENT_ID)
                    .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }
    @Nested
    @DisplayName("대 댓글 삭제")
    class deleteReComments{
        @Test
        @WithMockUser(username=AUTHORIZED_USER)
        @DisplayName("대 댓글 작성자가 자신의 대 댓글을 삭제")
        void deleteReCommentsAuthorized() throws Exception {
            when(commentService.getAuthorIdOfComment(COMMENT_ID,true))
                    .thenReturn(Long.valueOf(AUTHORIZED_USER));
            mvc.perform(delete("/comments/recomments/"+RE_COMMENT_ID)
                    .with(csrf()))
                    .andExpect(status().isOk());
        }
        @Test
        @WithMockUser(username=AUTHORIZED_USER)
        @DisplayName("댓글 삭제")
        void deleteReCommentsUnAuthorized() throws Exception {
            Long ownerId = 999L;
            when(commentService.getAuthorIdOfComment(COMMENT_ID,true))
                    .thenReturn(ownerId);
            mvc.perform(delete("/comments/recomments/"+RE_COMMENT_ID)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("포스트에 달린 댓글 가져오기")
    class getComments{

        @Test
        @DisplayName("메모에 딸린 댓글들 가져오기")
        @WithMockUser(username=SecurityContextUtils.ANONYMOUS_USER_ID_STRING)
        void getCommentsOfPost() throws Exception {
            List<CommentListDto> mockCommentList = createMockCommentList();
            when(commentService.getCommentsAtPost(PostType.MEMO,MEMO_ID, SecurityContextUtils.ANONYMOUS_USER_ID))
                    .thenReturn(mockCommentList);
            MvcResult result = mvc.perform(get("/comments/memo/" + MEMO_ID))
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            List<CommentListDto> commentList = JsonPath.read(responseBody, "$");
            assertThat(commentList).hasSize(3);
        }
        @Test
        @DisplayName("잘못된 포스트타입의 Path 로 요청을 보낼 경우")
        @WithMockUser(username=AUTHORIZED_USER)
        void invalidRequestPathVariableOfPostType() throws Exception {
            MvcResult result = mvc.perform(get("/comments/sth/" + MEMO_ID))
                    .andExpect(status().is5xxServerError())
                    .andReturn();
            assertThat(result.getResolvedException() instanceof IllegalArgumentException);
        }



        private List<CommentListDto> createMockCommentList(){
            CommentListDto firstComment = CommentListDto.builder()
                    .id(1L)
                    .commentText("1번댓글 내용")
                    .authorImagePath("작성자 이미지 경로")
                    .authorName("작성자_이름")
                    .authorId(1L)
                    .isLike(false)
                    .likes(0L)
                    .reCommentsNumber(0L)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();
            CommentListDto secondComment = CommentListDto.builder()
                    .id(2L)
                    .commentText("1번댓글 내용")
                    .authorImagePath("작성자 이미지 경로")
                    .authorName("작성자_이름")
                    .authorId(1L)
                    .isLike(false)
                    .likes(0L)
                    .reCommentsNumber(0L)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();
            CommentListDto thirdComment = CommentListDto.builder()
                    .id(3L)
                    .commentText("1번댓글 내용")
                    .authorImagePath("작성자 이미지 경로")
                    .authorName("작성자_이름")
                    .authorId(1L)
                    .isLike(false)
                    .likes(0L)
                    .reCommentsNumber(0L)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();

            return List.of(firstComment,secondComment,thirdComment);
        }
    }
    @Nested
    @DisplayName("대 댓글 가져오기")
    class getReComments{
        @Test
        @WithMockUser(username=SecurityContextUtils.ANONYMOUS_USER_ID_STRING)
        @DisplayName("댓글에 달린 대 댓글들을 모두 가져옵니다.")
        void getReCommentsOfComment() throws Exception {
            List<ReCommentListDto> mockReCommentList = createMockReCommentList();
            when(commentService.getReCommentsAtComments(COMMENT_ID, SecurityContextUtils.ANONYMOUS_USER_ID))
                    .thenReturn(mockReCommentList);

            MvcResult result = mvc.perform(get("/comments/recomments/" + COMMENT_ID))
                    .andExpect(status().isOk())
                    .andReturn();
            String responseBody = result.getResponse().getContentAsString();
            List<ReCommentListDto> reCommentList = JsonPath.read(responseBody, "$");

            assertThat(reCommentList).hasSize(3);
        }
        private List<ReCommentListDto> createMockReCommentList(){
            ReCommentListDto firstReComment = ReCommentListDto.builder()
                    .id(1L)
                    .commentText("1번 대댓글 내용")
                    .authorImagePath("작성자 이미지 경로")
                    .authorName("작성자_이름")
                    .authorId(1L)
                    .isLike(false)
                    .likes(0L)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();
            ReCommentListDto secondReComment = ReCommentListDto.builder()
                    .id(2L)
                    .commentText("2번 대댓글 내용")
                    .authorImagePath("작성자 이미지 경로")
                    .authorName("작성자_이름")
                    .authorId(1L)
                    .isLike(false)
                    .likes(0L)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();
            ReCommentListDto thirdReComment = ReCommentListDto.builder()
                    .id(3L)
                    .commentText("3번 대댓글 내용")
                    .authorImagePath("작성자 이미지 경로")
                    .authorName("작성자_이름")
                    .authorId(1L)
                    .isLike(false)
                    .likes(0L)
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();
            return List.of(firstReComment,secondReComment,thirdReComment);
        }
    }

    @Nested
    @DisplayName("댓글/대댓글 좋아요 로직")
    class likeComments{
        @Test
        @WithMockUser(AUTHORIZED_USER)
        @DisplayName("댓글 좋아요를 할 경우")
        void likeCommentAuthorized() throws Exception {
            mvc.perform(post("/comments/like/"+COMMENT_ID)
                    .with(csrf())
                    .param("isReComment","true"))
                    .andExpect(status().isOk());
        }
        @Test
        @WithMockUser(SecurityContextUtils.ANONYMOUS_USER_ID_STRING)
        @DisplayName("로그인하지 않은 유저가 좋아요를 할 경우")
        void likeComment() throws Exception {
            mvc.perform(post("/comments/like/"+COMMENT_ID)
                            .with(csrf())
                            .param("isReComment","true"))
                    .andExpect(status().isUnauthorized());
        }
        @Test
        @WithMockUser(username=AUTHORIZED_USER)
        @DisplayName("했던 좋아요를 취소한 경우")
        void cancelLikeOfComment() throws Exception{
            mvc.perform(delete("/comments/like/"+COMMENT_ID)
                    .with(csrf())
                    .param("isReComment","true"))
                    .andExpect(status().isOk());
        }
    }

}