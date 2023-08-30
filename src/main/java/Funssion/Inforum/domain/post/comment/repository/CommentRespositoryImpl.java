package Funssion.Inforum.domain.post.comment.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.CreatedRowException;
import Funssion.Inforum.domain.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
@Repository
public class CommentRespositoryImpl implements CommentRepository{
    private final JdbcTemplate template;
    public CommentRespositoryImpl(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }
    @Override
    public void createComment(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into comment.info (author_id, author_image_path, author_name, post_type, post_id, comment_text, created_date)"
                + "values (?, ?, ?, ?, ?, ?, ?)";
        int updatedRow = template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
            psmt.setLong(1, comment.getAuthorId());
            psmt.setString(2, comment.getAuthorImagePath());
            psmt.setString(3, comment.getAuthorName());
            psmt.setString(4, String.valueOf(comment.getPostTypeWithComment()));
            psmt.setLong(5, comment.getPostId());
            psmt.setString(6, comment.getCommentText());
            psmt.setDate(7, comment.getCreatedDate());
            return psmt;
        }, keyHolder);

        if (updatedRow != 1){
            throw new CreatedRowException("댓글 저장에 실패하였습니다.");
        }
    }

    @Override
    public ReComment createReComment(ReCommentSaveDto commentSaveDto) {
        return null;
    }

    @Override
    public List<CommentListDto> getCommentsAtPost(PostType postType, Long postId) {
        return null;
    }

    @Override
    public List<ReCommentListDto> getReCommentsAtComment(Long parentCommentId) {
        return null;
    }

    @Override
    public LikeResponseDto likeComment(PostType postType, Long commentId) {
        return null;
    }
}
