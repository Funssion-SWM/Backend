package Funssion.Inforum.domain.post.comment.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Date;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test") //test profile
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@Sql("classpath:CommentRepository.sql")
class CommentRepositoryImplTest {
    @Autowired
    DataSource dataSource;
    @Autowired
    CommentRepository commentRepository;
    @BeforeEach
    void beforeEach(){
        Comment comment = new Comment(1L,
                new MemberProfileEntity("set_image_path","set_nickname","set_introduce","set_tag"),
                Date.valueOf(now()),
                null,
                new CommentSaveDto(PostType.MEMO,1L,"set_text"));
        commentRepository.createComment(comment);
    }

    @Test
    @DisplayName("올바른 댓글 형식에서의 댓글 저장")
    void createValidComment(){
        Comment comment = new Comment(1L,
                new MemberProfileEntity("test_image_path","test_nickname","test_introduce","test_tag"),
                Date.valueOf(now()),
                null,
                new CommentSaveDto(PostType.MEMO,1L,"comment_text"));
        assertEquals(commentRepository.createComment(comment),2);
    }

//    @Test
//    @DisplayName("")
}