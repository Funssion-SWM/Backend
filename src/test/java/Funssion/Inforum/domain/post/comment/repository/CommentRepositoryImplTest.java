package Funssion.Inforum.domain.post.comment.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test") //test profile
@SpringBootTest
@ContextConfiguration
class CommentRepositoryImplTest {
    @Autowired
    DataSource dataSource;
    @Autowired
    CommentRepository commentRepository;

    private MemberProfileEntity testUserProfileEntity = new MemberProfileEntity("회원 프로필 이미지 저장 경로","회원 닉네임","회원 자기소개","회원 개인 태그");
    @BeforeAll
    static void setup(@Autowired DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("CommentRepository.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @BeforeEach
    void beforeEach(){
        Comment comment = new Comment(1L,
                testUserProfileEntity,
                Date.valueOf(now()),
                null,
                new CommentSaveDto(PostType.MEMO,1L,"댓글 내용 저장"));
        commentRepository.createComment(comment);
    }

    @Test
    @DisplayName("올바른 댓글 형식에서의 댓글 저장 성공")
    void createValidComment(){
        Comment comment = new Comment(1L,
                testUserProfileEntity,
                Date.valueOf(now()),
                null,
                new CommentSaveDto(PostType.MEMO,1L,"댓글 내용 저장"));
        assertThatCode(()->commentRepository.createComment(comment)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("올바른 댓글 형식의 댓글 수정 성공")
    void updateComment(){
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("수정된 댓글 내용");
        commentRepository.updateComment(commentUpdateDto,1L); //Before Each로 넣어놓은 댓글 수정
        assertThat(commentRepository.getCommentsAtPost(PostType.MEMO, 1L, 1L).get(0).getCommentText())
                .isEqualTo("수정된 댓글 내용");
    }
//    @Test
//    @DisplayName("댓글 삭제 성공")
//    void deleteComment(){
//        assertThatCode(
//                ()->commentRepository.deleteComment(1L))
//                .doesNotThrowAnyException();
//    }

    @Test
    @DisplayName("올바른 대댓글 형식의 대댓글 저장")
    void createReComment(){
        assertThatCode(
                ()->commentRepository.createReComment(new ReComment(1L,
                testUserProfileEntity,
                Date.valueOf(now()),
                null,
                1L,
                "대댓글 내용")))
                .doesNotThrowAnyException();
    }



    @Test
    @DisplayName("댓글이 존재하지 않지만, 대댓글 저장할 경우")
    void createReCommentWhenParentCommentDoestNotExist(){
        assertThatThrownBy(
                ()->commentRepository.createReComment(new ReComment(1L,
                        testUserProfileEntity,
                        Date.valueOf(now()),
                        null,
                        2L,
                        "대댓글 내용")))
                .isInstanceOf(NotFoundException.class);
    }


}