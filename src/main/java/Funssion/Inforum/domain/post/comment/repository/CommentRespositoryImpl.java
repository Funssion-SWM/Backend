package Funssion.Inforum.domain.post.comment.repository;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.CreateFailException;
import Funssion.Inforum.common.exception.UnAuthorizedException;
import Funssion.Inforum.common.exception.UpdateFailException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.IsSuccessResponseDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
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
            throw new CreateFailException("댓글 저장에 실패하였습니다.");
        }
    }

    @Override
    public IsSuccessResponseDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId) {
        checkAuthorization(CRUDType.UPDATE, commentId);
        String sql = "update comment.info set comment_text = ?, updated_date = ? where id = ?";
        if (template.update(sql, commentUpdateDto.getCommentText(), LocalDateTime.now(), commentId) == 0) {
            throw new UpdateFailException("댓글 수정에 실패하였습니다.");
        }

        return new IsSuccessResponseDto(true, "댓글이 수정되었습니다.");

    }

    @Override
    public IsSuccessResponseDto deleteComment(Long commentId) {
        checkAuthorization(CRUDType.DELETE, commentId);
        String sql = "delete from comment.info where id = ?";
        if(template.update(sql, commentId) == 0){
            throw new UpdateFailException("댓글 삭제에 실패하였습니다.");
        }

        return new IsSuccessResponseDto(true,"댓글이 삭제되었습니다.");

    }
    @Override
    public void createReComment(ReCommentSaveDto commentSaveDto) {

    }


    @Override
    public List<CommentListDto> getCommentsAtPost(PostType postType, Long postId) {
        String sql = "select id,author_id,author_image_path, author_name, likes, re_comments, comment_text, created_date, updated_date " +
                "from comment.info " +
                "where post_type = ?";
        return template.query(sql,commentRowMapper(),postType.toString());
    }

    private RowMapper<CommentListDto> commentRowMapper() {
        return ((rs,rowNum)->
            CommentListDto.builder()
                    .id(rs.getLong("id"))
                    .authorId(rs.getLong("author_id"))
                    .authorName(rs.getString("author_name"))
                    .authorImagePath(rs.getString("author_image_path"))
                    .commentText(rs.getString("comment_text"))
                    .createdDate(rs.getDate("created_date"))
                    .updatedDate(rs.getDate("updated_date"))
                    .likes(rs.getLong("likes"))
                    .reComments(rs.getLong("re_comments"))
                    .build());
    };

    @Override
    public List<ReCommentListDto> getReCommentsAtComment(Long parentCommentId) {
        return null;
    }

    @Override
    public LikeResponseDto likeComment(PostType postType, Long commentId) {
        return null;
    }


    private Long findAuthorIdByCommentId(Long commentId){
        String sql = "select author_id from comment.info where id = ?";
        try {
            return template.queryForObject(sql, Long.class, commentId);
        }catch(EmptyResultDataAccessException e){
            throw new NotFoundException("comment not found",e);
        }
    }
    private void checkAuthorization(CRUDType crudType, Long commentId) {
        Long userId = AuthUtils.getUserId(crudType);
        if (!userId.equals(findAuthorIdByCommentId(commentId))) {
            throw new UnAuthorizedException("Permission denied to "+crudType.toString());
        }
    }
}
