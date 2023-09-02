package Funssion.Inforum.domain.post.comment.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.sql.Date;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test") //test profile
@SpringBootTest
@Sql("classpath:CommentRepository.sql")
class CommentRepositoryImplTest {
    @Autowired
    DataSource dataSource;
    @Autowired
    CommentRepositoryImpl commentRepository;
    @Test
    @DisplayName("댓글 생성 체크")
    void createComment(){
        Comment comment = new Comment(1L,new MemberProfileEntity("test_image_path","test_nickname","test_introduce","test_tag"),
                Date.valueOf(now()),null,new CommentSaveDto(PostType.MEMO,1L,"coment_text"));
        assertEquals(commentRepository.createComment(comment),1);
    }
}