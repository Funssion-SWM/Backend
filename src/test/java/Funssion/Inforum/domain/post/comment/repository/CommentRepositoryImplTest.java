package Funssion.Inforum.domain.post.comment.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ActiveProfiles("test") //test profile
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
//@Sql("classpath:CommentRepository.sql")
class CommentRepositoryImplTest {
    @Autowired
    DataSource dataSource;
    @Autowired
    CommentRepository commentRepository;

    @BeforeAll
    static void setup(@Autowired DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            // you'll have to make sure conn.autoCommit = true (default for e.g. H2)
            // e.g. url=jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1;MODE=MySQL
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("CommentRepository.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @BeforeEach
    void beforeEach(){
        Comment comment = new Comment(1L,
                new MemberProfileEntity("회원 프로필 이미지 저장 경로","회원 닉네임","회원 자기소개","회원 개인 태그"),
                Date.valueOf(now()),
                null,
                new CommentSaveDto(PostType.MEMO,1L,"댓글 내용 저장"));
        commentRepository.createComment(comment);
        commentRepository.likeComment(1L,false,1L);
    }

    @Test
    @DisplayName("올바른 댓글 형식에서의 댓글 저장")
    void createValidComment(){
        Comment comment = new Comment(1L,
                new MemberProfileEntity("프로필 이미지 경로","회원 닉네임","회원 자기소개","회원 개인 태그"),
                Date.valueOf(now()),
                null,
                new CommentSaveDto(PostType.MEMO,1L,"댓글 내용 저장"));
        assertDoesNotThrow(()->commentRepository.createComment(comment));
    }

    @Test
    @DisplayName("올바른 댓글 형식의 댓글 수정")
    void updateComment(){
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("수정된 댓글 내용");
        commentRepository.updateComment(commentUpdateDto,1L); //Before Each로 넣어놓은 댓글 수정
        commentRepository.getCommentsAtPost(PostType.MEMO,1L,1L);
    }

}