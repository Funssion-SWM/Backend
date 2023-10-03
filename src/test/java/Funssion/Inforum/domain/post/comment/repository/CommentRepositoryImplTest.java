package Funssion.Inforum.domain.post.comment.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.etc.UpdateFailException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class CommentRepositoryImplTest {
    @Autowired
    DataSource dataSource;
    @Autowired
    CommentRepository commentRepository;

    private MemberProfileEntity testUserProfileEntity = new MemberProfileEntity("회원 프로필 이미지 저장 경로", "회원 닉네임", "회원 자기소개", List.of("tag1,tag2"));

    @BeforeEach
    void beforeEach() {
        Comment comment = new Comment(1L,
                testUserProfileEntity,
                LocalDateTime.now(),
                null,
                new CommentSaveDto(PostType.MEMO, 1L, "댓글 내용 저장"));
        commentRepository.createComment(comment);
    }


    @Test
    @DisplayName("올바른 댓글 형식에서의 댓글 저장 성공")
    void createValidComment() {
        Comment comment = new Comment(1L,
                testUserProfileEntity,
                LocalDateTime.now(),
                null,
                new CommentSaveDto(PostType.MEMO, 1L, "댓글 내용 저장"));
        assertThatCode(() -> commentRepository.createComment(comment)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("올바른 댓글 형식의 댓글 수정 성공")
    void updateComment() {
        Long commentIdPutBeforeTest = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L).get(0).getId();
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("수정된 댓글 내용");
        commentRepository.updateComment(commentUpdateDto, commentIdPutBeforeTest); //Before Each로 넣어놓은 댓글 수정
        assertThat(commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L).get(0).getCommentText())
                .isEqualTo("수정된 댓글 내용");
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment() {
        Comment comment = new Comment(1L,
                testUserProfileEntity,
                LocalDateTime.now(),
                null,
                new CommentSaveDto(PostType.MEMO, 1L, "댓글 내용 저장"));
        Comment createdComment = commentRepository.createComment(comment);
        assertThatCode(
                () -> commentRepository.deleteComment(createdComment.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("올바른 대댓글 형식의 대댓글 저장 및 댓글애 달린 대댓글 개수 갱신")
    void createReComment() {

        List<CommentListDto> commentsAtPostBeforeReCommentsAdded = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L);
        Long getFirstCommentId = commentsAtPostBeforeReCommentsAdded.get(0).getId();
        assertThatCode(
                () -> commentRepository.createReComment(new ReComment(1L,
                        testUserProfileEntity,
                        LocalDateTime.now(),
                        null,
                        getFirstCommentId,
                        "대댓글 내용")))
                .doesNotThrowAnyException();
        List<CommentListDto> commentsAtPostAfterReCommentsAdded = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L);
        assertThat(commentsAtPostAfterReCommentsAdded.get(0).getReCommentsNumber()).isEqualTo(1L);
    }

    @Test
    @DisplayName("대댓글 삭제 및 댓글에 달린 대댓글 수 갱신")
    void deleteReComment() {

        List<CommentListDto> commentsAtPostBeforeReCommentsDeleted = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L);
        Long getFirstCommentId = commentsAtPostBeforeReCommentsDeleted.get(0).getId();
        assertThatCode(
                () -> commentRepository.createReComment(new ReComment(1L,
                        testUserProfileEntity,
                        LocalDateTime.now(),
                        null,
                        getFirstCommentId,
                        "대댓글 내용")))
                .doesNotThrowAnyException();
        ReCommentListDto getCreatedFirstReComment = commentRepository.getReCommentsAtComment(getFirstCommentId, 1L).get(0);
        assertThatCode(
                () -> commentRepository.deleteReComment(getCreatedFirstReComment.getId())).doesNotThrowAnyException();
        List<CommentListDto> commentsAtPostAfterReCommentsDeleted = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L);
        assertThat(commentsAtPostAfterReCommentsDeleted.get(0).getReCommentsNumber()).isEqualTo(0L);
    }


    @Test
    @DisplayName("댓글이 존재하지 않지만, 대댓글 저장할 경우")
    void createReCommentWhenParentCommentDoestNotExist() {
        assertThatThrownBy(
                () -> commentRepository.createReComment(new ReComment(1L,
                        testUserProfileEntity,
                        LocalDateTime.now(),
                        null,
                        100L,
                        "대댓글 내용")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("대댓글이 존재하고 이를 수정하는 경우")
    void updateReCommentWhenItIsExist() {
        Long commentIdPutBeforeTest = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L).get(0).getId();
        commentRepository.createReComment(new ReComment(1L,
                testUserProfileEntity,
                LocalDateTime.now(),
                null,
                commentIdPutBeforeTest,
                "대댓글 내용"));
        ReCommentListDto getFirstReCommentBeforeUpdate = commentRepository.getReCommentsAtComment(commentIdPutBeforeTest, 1L).get(0);
        IsSuccessResponseDto isUpdateSuccess = commentRepository.updateReComment(new ReCommentUpdateDto("대댓글 수정"), getFirstReCommentBeforeUpdate.getId());
        assertThat(isUpdateSuccess.getIsSuccess()).isEqualTo(true);
        ReCommentListDto getFirstReCommentAfterUpdate = commentRepository.getReCommentsAtComment(commentIdPutBeforeTest, 1L).get(0);
        assertThat(getFirstReCommentAfterUpdate.getCommentText()).isEqualTo("대댓글 수정");
    }

    @Test
    @DisplayName("대댓글이 존재하지 않는데 수정하는 경우")
    void updateReCommentWhenItIsNotExist() {
        Long inValidIdOfReCommentId = 4L;
        assertThatThrownBy(() -> commentRepository.updateReComment(new ReCommentUpdateDto("대댓글 수정"), inValidIdOfReCommentId)).isExactlyInstanceOf(UpdateFailException.class);
    }


    @Test
    @DisplayName("댓글 좋아요가 댓글 리스트 반환에 포함")
    void commentsGetLikes() {
        Long commentIdBeforeLike = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L).get(0).getId();
        Long userIdWhoLikesComment1 = 10L;
        Long userIdWhoLikesComment2 = 11L;
        assertThat(commentRepository.likeComment(commentIdBeforeLike, false, userIdWhoLikesComment1).getLikes()).isEqualTo(1L);
        assertThat(commentRepository.likeComment(commentIdBeforeLike, false, userIdWhoLikesComment2).getLikes()).isEqualTo(2L);
        assertThat(commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L).get(0).getLikes()).isEqualTo(2L);
    }

    @Test
    @DisplayName("사용자가 특정 댓글 좋아요 했는지 댓글 리스트에 포함")
    void doesUserLikeComment() {

        Comment comment = new Comment(1L,
                testUserProfileEntity,
                LocalDateTime.now(),
                null,
                new CommentSaveDto(PostType.MEMO, 1L, "댓글 내용 저장"));
        commentRepository.createComment(comment);
        List<CommentListDto> commentsBeforeLike = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L);
        CommentListDto commentBeforeUserLike = commentsBeforeLike.get(1);
        assertThat(commentBeforeUserLike.getIsLike()).isEqualTo(false);
        Long userIdWhoLikesComment = 10L;
        Long userIdLikesNothing = 11L;
        assertThat(commentBeforeUserLike.getLikes()).isEqualTo(0L);
        commentRepository.likeComment(commentBeforeUserLike.getId(), false, userIdWhoLikesComment);
        List<CommentListDto> commentsAfterLike = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, userIdWhoLikesComment);
        List<CommentListDto> commentsAfterNothing = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, userIdLikesNothing);
        Optional<CommentListDto> commentAfterUserLike = commentsAfterLike.stream().filter(c -> c.getId().equals(commentBeforeUserLike.getId())).findFirst();
        Optional<CommentListDto> commentAfterNothing = commentsAfterNothing.stream().filter(c -> c.getId().equals(commentBeforeUserLike.getId())).findFirst();
        assertThat(commentAfterUserLike.get().getIsLike()).isEqualTo(true);
        assertThat(commentAfterNothing.get().getIsLike()).isEqualTo(false);

    }

    @Test
    @DisplayName("사용자가 특정 댓글 좋아요를 취소했는지 댓글 리스트에 포함")
    void doesUserCancelLikeComment() {
        CommentListDto firstComment = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L).get(0);
        Long userIdWhoLikesComment = 10L;
        commentRepository.likeComment(firstComment.getId(), false, userIdWhoLikesComment);
        CommentListDto firstCommentAfterLike = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, userIdWhoLikesComment).get(0);
        assertThat(firstCommentAfterLike.getLikes()).isEqualTo(1L);
        assertThat(firstCommentAfterLike.getIsLike()).isEqualTo(true);
        commentRepository.cancelLikeComment(firstComment.getId(), false, userIdWhoLikesComment);
        CommentListDto firstCommentAfterCancelLike = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, userIdWhoLikesComment).get(0);
        assertThat(firstCommentAfterCancelLike.getLikes()).isEqualTo(0L);
        assertThat(firstCommentAfterCancelLike.getIsLike()).isEqualTo(false);

    }

    @Test
    @DisplayName("사용자가 대댓글에 좋아요를 누르면, 좋아요 개수와, 자신이 좋아요를 눌렀는지 확인")
    void doesUserLikeReComment(){
        CommentListDto firstComment = commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L).get(0);
        Long reCommentWriterId = 10L;
        Long reCommentLikerId = 9L;

        commentRepository.createReComment(new ReComment(reCommentWriterId,
                testUserProfileEntity,
                LocalDateTime.now(),
                null,
                firstComment.getId(),
                "대댓글 내용"));

        ReCommentListDto firstReCommentOfComment = commentRepository.getReCommentsAtComment(firstComment.getId(), reCommentLikerId).get(0);
        assertThat(commentRepository.likeComment(firstReCommentOfComment.getId(), true, reCommentLikerId).getLikes()).isEqualTo(1L);
        List<ReCommentListDto> reCommentsAtComment = commentRepository.getReCommentsAtComment(firstComment.getId(), reCommentLikerId);
        assertThat(reCommentsAtComment.size()).isEqualTo(1);
        assertThat(reCommentsAtComment.get(0).getLikes()).isEqualTo(1L);
        assertThat(reCommentsAtComment.get(0).getIsLike()).isEqualTo(true);
    }

}