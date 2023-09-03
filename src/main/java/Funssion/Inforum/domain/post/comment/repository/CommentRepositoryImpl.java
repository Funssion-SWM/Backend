package Funssion.Inforum.domain.post.comment.repository;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.CreateFailException;
import Funssion.Inforum.common.exception.UpdateFailException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.IsSuccessResponseDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.comment.exception.DuplicateLikeException;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@Slf4j
public class CommentRepositoryImpl implements CommentRepository{
    private final JdbcTemplate template;
    public CommentRepositoryImpl(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }
    @Override
    public Long createComment(Comment comment) {
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
            psmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now())); // 생성자에서부터 바꿔야할 필요 있음. 반드시 리뷰할것.
            return psmt;
        }, keyHolder);

        if (updatedRow != 1){
            throw new CreateFailException("댓글 저장에 실패하였습니다.");
        }
        return keyHolder.getKey().longValue();
    }

    @Override
    public IsSuccessResponseDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId) {
        String sql = "update comment.info set comment_text = ?, updated_date = ? where id = ?";
        if (template.update(sql, commentUpdateDto.getCommentText(), LocalDateTime.now(), commentId) == 0) {
            throw new UpdateFailException("댓글 수정에 실패하였습니다.");
        }

        return new IsSuccessResponseDto(true, "댓글이 수정되었습니다.");

    }

    @Override
    public IsSuccessResponseDto updateReComment(ReCommentUpdateDto reCommentUpdateDto, Long reCommentId) {
        String sql = "update comment.re_comments set comment_text = ?, updated_date = ? where id = ?";
        if (template.update(sql, reCommentUpdateDto.getCommentText(), LocalDateTime.now(), reCommentId) == 0) {
            throw new UpdateFailException("대 댓글 수정에 실패하였습니다.");
        }
        return new IsSuccessResponseDto(true, "대댓글이 수정되었습니다.");

    }

    @Override
    public IsSuccessResponseDto deleteComment(Long commentId) {
        String sql = "delete from comment.info where id = ?";
        if(template.update(sql, commentId) == 0){
            throw new UpdateFailException("댓글 삭제에 실패하였습니다.");
        }

        return new IsSuccessResponseDto(true,"댓글이 삭제되었습니다.");
    }

    @Override
    public IsSuccessResponseDto deleteReComment(Long reCommentId) {
        String sql = "delete from comment.re_comments where id =?";
        if(template.update(sql, reCommentId) == 0){
            throw new UpdateFailException("대댓글 삭제에 실패하였습니다.");
        }
        return new IsSuccessResponseDto(true,"대댓글이 삭제되었습니다.");
    }

    @Override
    public void createReComment(ReComment reComment) {
        if (findParentCommentById(reComment.getParentCommentId()).isEmpty())
            throw new NotFoundException("대댓글을 등록하기 위한 댓글이 존재하지 않습니다.");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into comment.re_comments (author_id, author_image_path, author_name, parent_id, comment_text, created_date)"
                + "values(?,?,?,?,?,?)";
        int updatedRow = template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
            psmt.setLong(1, reComment.getAuthorId());
            psmt.setString(2, reComment.getAuthorImagePath());
            psmt.setString(3, reComment.getAuthorName());
            psmt.setLong(4, reComment.getParentCommentId());
            psmt.setString(5, reComment.getCommentText());
            psmt.setDate(6, reComment.getCreatedDate());
            return psmt;
        }, keyHolder);
        if (updatedRow != 1){
            throw new CreateFailException("대댓글 저장에 실패하였습니다.");
        }

    }


    @Override
    public List<CommentListDto> getCommentsAtPost(PostType postType, Long postId, Long userId) {
        String sql =
                "SELECT COMMENT.id, COMMENT.author_id, COMMENT.author_image_path, COMMENT.author_name, COMMENT.likes, COMMENT.re_comments, COMMENT.comment_text, COMMENT.created_date, COMMENT.updated_date,"+
                "CASE WHEN whoLikeCOMMENT.user_id = ? THEN TRUE ELSE FALSE END AS is_liked "+
                "FROM (SELECT id, author_id, author_image_path, author_name, likes, re_comments, comment_text, created_date, updated_date " +
                "FROM comment.info where post_type = ? and post_id = ?) COMMENT "+
                "LEFT JOIN member.like_comment whoLikeCOMMENT ON COMMENT.id = whoLikeCOMMENT.comment_id AND whoLikeCOMMENT.user_id = ? AND whoLikeCOMMENT.is_recomment = false " +
                        "order by COMMENT.created_date";
//        String sql = "SELECT c.id, c.author_id, c.author_image_path, c.author_name, c.likes, c.re_comments, c.comment_text, c.created_date, c.updated_date, " +
//                "CASE WHEN m.user_id = ? THEN TRUE ELSE FALSE END AS is_liked " + // 로그인된 사용자가 좋아요를 했는지 확인하는 부분
//                "FROM comment.info c " +
//                "LEFT JOIN member.like_comment m ON c.id = m.comment_id AND m.is_recomment = false " +
//                "WHERE c.post_type = ?";
        return template.query(sql, commentListRowMapper(), userId, postType.toString(),postId, userId);
    }

    @Override
    public List<ReCommentListDto> getReCommentsAtComment(Long parentCommentId,Long userId) {
        String sql =
                "SELECT DISTINCT RECOMMENT.id, RECOMMENT.author_id, RECOMMENT.author_image_path, RECOMMENT.author_name, RECOMMENT.likes, RECOMMENT.comment_text, RECOMMENT.created_date, RECOMMENT.updated_date,"+
                "CASE WHEN whoLikeCOMMENT.user_id = ? THEN TRUE ELSE FALSE END AS is_liked "+
                "FROM (SELECT id, author_id, author_image_path, author_name, likes, comment_text, created_date, updated_date " +
                "FROM comment.re_comments where parent_id = ?) RECOMMENT "+
                "LEFT JOIN member.like_comment whoLikeCOMMENT ON RECOMMENT.id = whoLikeCOMMENT.comment_id AND whoLikeCOMMENT.user_id = ? AND whoLikeCOMMENT.is_recomment = true " +
                        "order by RECOMMENT.created_date";
        return template.query(sql,reCommentListRowMapper(),userId,parentCommentId,userId);
    }

    @Override
    public LikeResponseDto likeComment(Long commentId, Boolean isReComment) {
        insertLikeOfMemberLikeCommentsTable(commentId, isReComment);
        Long howManyLikesAfterLike = updateLikesOfCommentsTable(commentId, isReComment,false);
        return new LikeResponseDto(true,howManyLikesAfterLike);
    }

    @Override
    public LikeResponseDto cancelLikeComment(Long commentId, Boolean isReComment) {
        deleteLikeOfMemberLikeCommentsTable(commentId, isReComment);
        Long howManyLikesAfterCancelLike = updateLikesOfCommentsTable(commentId, isReComment, true);
        return new LikeResponseDto(false,howManyLikesAfterCancelLike);
    }


    private Long updateLikesOfCommentsTable(Long commentId, boolean isReComment, boolean isCancel) {
        Long likesOfComment = getLikesOfComment(commentId, isReComment); // 좋아요 반영 이전
        String sql = "";
        if (isReComment) sql = "update comment.re_comments set likes = ? where id = ?";
        else sql = "update comment.info set likes = ? where id = ?";
        if (isCancel) {
            template.update(sql,likesOfComment-1,commentId);
            return likesOfComment - 1;
        }
        else {
            template.update(sql,likesOfComment+1, commentId);
            return likesOfComment + 1;
        }

    }

    private void insertLikeOfMemberLikeCommentsTable(Long commentId, boolean isReComment) {
        Long userId = AuthUtils.getUserId(CRUDType.CREATE);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into member.like_comment (user_id,comment_id,is_recomment)" +
                "values(?,?,?)";
        try {
            int updatedRow = template.update(con -> {
                PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
                psmt.setLong(1, userId);
                psmt.setLong(2, commentId);
                psmt.setBoolean(3, isReComment);
                return psmt;
            }, keyHolder);
            if (updatedRow != 1) {
                throw new CreateFailException("댓글 좋아요 반영에 실패하였습니다.");
            }
        } catch (DuplicateKeyException e){
            throw new DuplicateLikeException("같은 댓글에 좋아요를 여러번 할 수 없습니다.");
        }
    }

    private void deleteLikeOfMemberLikeCommentsTable(Long commentId, boolean isReComment) {
        Long userId = AuthUtils.getUserId(CRUDType.DELETE);
        String sql = "delete from member.like_comment where comment_id = ? and user_id = ? and is_recomment = ?";
        int updatedRow = template.update(sql, commentId, userId, isReComment);
        if (updatedRow != 1){
            throw new CreateFailException("댓글 좋아요 취소에 실패하였습니다.");
        }
    }


    private Optional<CommentListDto> findParentCommentById(Long commentId){
        String sql = "select id,author_id,author_image_path, author_name, likes, re_comments, comment_text, created_date, updated_date " +
                "from comment.info " +
                "where id = ?";
        try {
            return Optional.of(template.queryForObject(sql, commentRowMapper(), commentId));
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    private Long getLikesOfComment(Long commentId,boolean isReComment){
        String sql = "";
        if (isReComment) sql = "select likes from comment.re_comments where id = ?";
        else sql ="select likes from comment.info where id = ?";
        try{
            return template.queryForObject(sql, Long.class, commentId);
        }catch(EmptyResultDataAccessException e){
            throw new NotFoundException("좋아요한 댓글을 찾을 수 없습니다.");
        }
    }
    @Override
    public Long findAuthorIdByCommentId(Long commentId, Boolean isReComment){
        String sql = "";
        if (isReComment) sql = "select author_id from comment.re_comments where id = ?";
        else sql ="select author_id from comment.info where id =?";
        try {
            return template.queryForObject(sql, Long.class, commentId);
        }catch(EmptyResultDataAccessException e){
            throw new NotFoundException("comment not found",e);
        }
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
    private RowMapper<CommentListDto> commentListRowMapper() {
        return ((rs,rowNum)->
                CommentListDto.builder()
                        .id(rs.getLong("id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .commentText(rs.getString("comment_text"))
                        .createdDate(rs.getDate("created_date"))
                        .updatedDate(rs.getDate("updated_date"))
                        .isLike(rs.getBoolean("is_liked"))
                        .likes(rs.getLong("likes"))
                        .reComments(rs.getLong("re_comments"))
                        .build());
    };
    private RowMapper<ReCommentListDto> reCommentRowMapper() {
        return ((rs,rowNum)->
                ReCommentListDto.builder()
                        .id(rs.getLong("id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .commentText(rs.getString("comment_text"))
                        .createdDate(rs.getDate("created_date"))
                        .updatedDate(rs.getDate("updated_date"))
                        .likes(rs.getLong("likes"))
                        .isLike(rs.getBoolean("is_liked"))
                        .build());
    };
    private RowMapper<ReCommentListDto> reCommentListRowMapper() {
        return ((rs,rowNum)->
                ReCommentListDto.builder()
                        .id(rs.getLong("id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .commentText(rs.getString("comment_text"))
                        .createdDate(rs.getDate("created_date"))
                        .updatedDate(rs.getDate("updated_date"))
                        .likes(rs.getLong("likes"))
                        .isLike(rs.getBoolean("is_liked"))
                        .build());
    };
}
