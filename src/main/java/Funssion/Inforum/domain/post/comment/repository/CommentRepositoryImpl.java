package Funssion.Inforum.domain.post.comment.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.etc.CreateFailException;
import Funssion.Inforum.common.exception.etc.UpdateFailException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.PostIdAndTypeInfo;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.comment.exception.DuplicateLikeException;
import Funssion.Inforum.domain.post.like.dto.response.LikeResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class CommentRepositoryImpl implements CommentRepository{
    private final JdbcTemplate template;
    public CommentRepositoryImpl(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }
    @Override
    public Comment createComment(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into post.comment (author_id, author_image_path, author_name, post_type, post_id, comment_text, created_date,author_rank)"
                + "values (?, ?, ?, ?, ?, ?, ?, ?)";
        int updatedRow = template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
            psmt.setLong(1, comment.getAuthorId());
            psmt.setString(2, comment.getAuthorImagePath());
            psmt.setString(3, comment.getAuthorName());
            psmt.setString(4, String.valueOf(comment.getPostTypeWithComment()));
            psmt.setLong(5, comment.getPostId());
            psmt.setString(6, comment.getCommentText());
            psmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now())); // 생성자에서부터 바꿔야할 필요 있음. 반드시 리뷰할것.
            psmt.setString(8,comment.getRank());
            return psmt;
        }, keyHolder);

        if (updatedRow != 01){
            throw new CreateFailException("댓글 저장에 실패하였습니다.");
        }
        return getCommentById(keyHolder.getKey().longValue());
    }
    private Comment getCommentById(Long commentId){
        String sql = "select id,post_id, author_id,author_image_path, author_name, author_rank, likes, recomments, post_type, comment_text, created_date, updated_date, author_rank, is_user_delete " +
                "from post.comment where id =?";
        return template.queryForObject(sql,commentRowMapper(),commentId);
    }
    private ReComment getRecommentById(Long recommentId) {
        String sql = "SELECT * " +
                "FROM post.recomment " +
                "WHERE id = ?";
        return template.queryForObject(sql, reCommentRowMapper(), recommentId);
    }
    public void updateProfileImageOfComment(Long userId, String authorProfileImagePath){
        String sql = "update post.comment " +
                "set author_image_path = ? " +
                "where author_id = ?";

        template.update(sql, authorProfileImagePath, userId);
    }

    public void updateProfileImageOfReComment(Long userId, String authorProfileImagePath){
        String sql = "update post.recomment " +
                "set author_image_path = ? " +
                "where author_id = ?";

        template.update(sql, authorProfileImagePath, userId);
    }

    @Override
    public PostIdAndTypeInfo getPostIdByCommentId(Long commentId) {
        String sql = "select post_id, post_type from post.comment where id = ?";
        return template.queryForObject(sql,postIdAndPostTypeRowMapper(),commentId);
    }
    public List<Comment> findIfUserRegisterAnotherCommentOfPost(Long userId, Long commentId){
        PostInfo postInfoOfComment = getPostInfoOfComment(userId, commentId);

        String sql = "select id,post_id, author_id,author_image_path, author_name, author_rank, likes, recomments, post_type, comment_text, created_date, updated_date, is_user_delete" +
                " from post.comment where author_id = ? and post_type = ? and post_id = ? and is_user_delete is false and id != ?";

        return template.query(sql, commentRowMapper(), userId,postInfoOfComment.getPostType().toString(),postInfoOfComment.getPostId(),commentId);
    }
    private PostInfo getPostInfoOfComment(Long userId, Long postId){
        String sql = "select post_id, post_type" +
                " from post.comment where author_id = ? and id = ?";
        return template.queryForObject(sql, postInfoRowMapper(), userId,postId);
    }
    @Override
    public IsSuccessResponseDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId) {
        String sql = "update post.comment set comment_text = ?, updated_date = ? where id = ?";
        if (template.update(sql, commentUpdateDto.getCommentText(), LocalDateTime.now(), commentId) == 0) {
            throw new UpdateFailException("댓글 수정에 실패하였습니다.");
        }

        return new IsSuccessResponseDto(true, "댓글이 수정되었습니다.");

    }

    @Override
    public IsSuccessResponseDto updateReComment(ReCommentUpdateDto reCommentUpdateDto, Long reCommentId) {
        String sql = "update post.recomment set comment_text = ?, updated_date = ? where id = ?";
        if (template.update(sql, reCommentUpdateDto.getCommentText(), LocalDateTime.now(), reCommentId) == 0) {
            throw new UpdateFailException("대 댓글 수정에 실패하였습니다.");
        }
        return new IsSuccessResponseDto(true, "대댓글이 수정되었습니다.");

    }



    @Override
    public IsSuccessResponseDto deleteCommentWhichHasRecomment(Long commentId) {
        String sql = "UPDATE post.comment " +
                "SET comment_text = '삭제된 댓글입니다.', is_user_delete = true, updated_date = current_timestamp " +
                "WHERE id = ?";
        if(template.update(sql, commentId) == 0){
            throw new UpdateFailException("댓글 삭제에 실패하였습니다.");
        }

        return new IsSuccessResponseDto(true,"댓글이 삭제되었습니다.");
    }

    @Override
    public IsSuccessResponseDto deleteComment(Long commentId) {
        String sql = "delete from post.comment where id = ?";
        if(template.update(sql, commentId) == 0){
            throw new UpdateFailException("댓글 삭제에 실패하였습니다.");
        }

        return new IsSuccessResponseDto(true,"댓글이 삭제되었습니다.");
    }

    @Override
    public IsSuccessResponseDto deleteReComment(Long reCommentId) {
        Long parentCommentId = getParentCommentId(reCommentId);
        deleteReCommentsInTable(reCommentId);
        Long reCommentCountOfComment = updateNumberOfReCommentsOfComment(parentCommentId, true);
        if(reCommentCountOfComment ==0){
            Comment parentComment = getCommentById(parentCommentId);
            if(parentComment.getIsUserDelete()) deleteComment(parentCommentId);
        }
        return new IsSuccessResponseDto(true,"대댓글이 삭제되었습니다.");
    }

    @Override
    public Long getRecommentsCountOfComment(Long commentId) {
        String sql = "SELECT count(id) " +
                "FROM post.recomment " +
                "WHERE parent_id = ?";
        return template.queryForObject(sql,Long.class,commentId);
    }

    private void deleteReCommentsInTable(Long reCommentId) {
        String sql = "delete from post.recomment where id =?";
        if(template.update(sql, reCommentId) == 0){
            throw new UpdateFailException("대댓글 삭제에 실패하였습니다.");
        }
    }

    private Long getParentCommentId(Long reCommentId) {
        String sql = "select parent_id from post.recomment where id = ?";
        return template.queryForObject(sql, Long.class, reCommentId);
    }

    @Override
    public ReComment createReComment(ReComment reComment) {
        if (findParentCommentById(reComment.getParentCommentId()).isEmpty())
            throw new NotFoundException("대댓글을 등록하기 위한 댓글이 존재하지 않습니다.");
        Long recommentId = insertReCommentInTable(reComment);
        updateNumberOfReCommentsOfComment(reComment.getParentCommentId(),false);
        return getRecommentById(recommentId);
    }

    private Long insertReCommentInTable(ReComment reComment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into post.recomment (author_id, author_image_path, author_name, parent_id, comment_text, created_date,author_rank)"
                + "values(?,?,?,?,?,?,?)";
        int updatedRow = template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
            psmt.setLong(1, reComment.getAuthorId());
            psmt.setString(2, reComment.getAuthorImagePath());
            psmt.setString(3, reComment.getAuthorName());
            psmt.setLong(4, reComment.getParentCommentId());
            psmt.setString(5, reComment.getCommentText());
            psmt.setTimestamp(6, Timestamp.valueOf(reComment.getCreatedDate()));
            psmt.setString(7, reComment.getRank());
            return psmt;
        }, keyHolder);
        if (updatedRow != 1){
            throw new CreateFailException("대댓글 저장에 실패하였습니다.");
        }

        return keyHolder.getKey().longValue();
    }

    private Long updateNumberOfReCommentsOfComment(Long commentId, Boolean isDelete) {
        String sql = isDelete? "update post.comment set recomments = recomments - 1 where id = ?"
                :"update post.comment set recomments = recomments + 1 where id = ?";
        int updatedRow = template.update(sql, commentId);
        if (updatedRow != 1){
            throw new UpdateFailException("대댓글 수 갱신에 실패하였습니다.");
        }
        return getRecommentsCountOfComment(commentId);
    }


    @Override
    public List<CommentListDto> getCommentsAtPost(PostType postType, Long postId, Long userId) {
        String sql =
                "SELECT COMMENT.id, COMMENT.author_id, COMMENT.author_image_path, COMMENT.author_name, COMMENT.author_rank, COMMENT.likes, COMMENT.recomments, COMMENT.comment_text, COMMENT.created_date, COMMENT.updated_date,"+
                "CASE WHEN whoLikeCOMMENT.user_id = ? THEN TRUE ELSE FALSE END AS is_liked "+
                "FROM (SELECT id, author_id, author_image_path, author_name, author_rank, likes, recomments, comment_text, created_date, updated_date " +
                "FROM post.comment where post_type = ? and post_id = ?) COMMENT "+
                "LEFT JOIN member.like_comment whoLikeCOMMENT ON COMMENT.id = whoLikeCOMMENT.comment_id AND whoLikeCOMMENT.user_id = ? AND whoLikeCOMMENT.is_recomment = false " +
                        "order by COMMENT.created_date";
//        String sql = "SELECT c.id, c.author_id, c.author_image_path, c.author_name, c.likes, c.recomments, c.comment_text, c.created_date, c.updated_date, " +
//                "CASE WHEN m.user_id = ? THEN TRUE ELSE FALSE END AS is_liked " + // 로그인된 사용자가 좋아요를 했는지 확인하는 부분
//                "FROM post.comment c " +
//                "LEFT JOIN member.like_comment m ON c.id = m.comment_id AND m.is_recomment = false " +
//                "WHERE c.post_type = ?";
        return template.query(sql, commentListRowMapper(), userId, postType.toString(),postId, userId);
    }

    @Override
    public List<ReCommentListDto> getReCommentsAtComment(Long parentCommentId,Long userId) {
        String sql =
                "SELECT DISTINCT RECOMMENT.id, RECOMMENT.author_id, RECOMMENT.author_image_path, RECOMMENT.author_name, RECOMMENT.author_rank, RECOMMENT.likes, RECOMMENT.comment_text, RECOMMENT.created_date, RECOMMENT.updated_date,"+
                "CASE WHEN whoLikeCOMMENT.user_id = ? THEN TRUE ELSE FALSE END AS is_liked "+
                "FROM (SELECT id, author_id, author_image_path, author_name, author_rank, likes, comment_text, created_date, updated_date " +
                "FROM post.recomment where parent_id = ?) RECOMMENT "+
                "LEFT JOIN member.like_comment whoLikeCOMMENT ON RECOMMENT.id = whoLikeCOMMENT.comment_id AND whoLikeCOMMENT.user_id = ? AND whoLikeCOMMENT.is_recomment = true " +
                        "order by RECOMMENT.created_date";
        return template.query(sql,reCommentListRowMapper(),userId,parentCommentId,userId);
    }

    @Override
    public LikeResponseDto likeComment(Long commentId, Boolean isReComment,Long userId) {
        insertLikeOfMemberLikeCommentsTable(commentId, isReComment,userId);
        Long howManyLikesAfterLike = updateLikesOfCommentsTable(commentId, isReComment,false);
        return new LikeResponseDto(true,howManyLikesAfterLike);
    }

    @Override
    public LikeResponseDto cancelLikeComment(Long commentId, Boolean isReComment,Long userId) {
        deleteLikeOfMemberLikeCommentsTable(commentId, isReComment,userId);
        Long howManyLikesAfterCancelLike = updateLikesOfCommentsTable(commentId, isReComment, true);
        return new LikeResponseDto(false,howManyLikesAfterCancelLike);
    }

    @Override
    public void plusCommentsCountOfPost(PostType postType, Long id) {
        String sql = sqlUpdatingDifferentPost(postType,id);
        if (template.update(sql, id) == 0) throw new NotFoundException("댓글 수 업데이트 실패. 게시글이 존재하지 않음");
    }
    private String sqlUpdatingDifferentPost(PostType postType, Long id){
        switch(postType){
            case MEMO:
                return "update post.memo set replies_count = replies_count + 1 where id = ?";
            case QUESTION:
                return "update post.question set replies_count = replies_count + 1 where id = ?";
            case ANSWER:
                return "update post.answer set replies_count = replies_count + 1 where id = ?";
            default:
                throw new IllegalArgumentException("잘못된 postType의 댓글입니다.");
        }
    }

    @Override
    public void subtractCommentsCountOfPost(PostIdAndTypeInfo postIdAndTypeInfo) {
        String sql = "";
        switch(postIdAndTypeInfo.getPostType()){
            case MEMO -> sql = "update post.memo set replies_count = replies_count - 1 where id = ?";
            case QUESTION -> sql = "update post.question set replies_count = replies_count - 1 where id = ?";
            case ANSWER -> sql = "update post.answer set replies_count = replies_count - 1 where id = ?";
        }
        if (template.update(sql,postIdAndTypeInfo.getPostId()) == 0) throw new NotFoundException("댓글 수 업데이트 실패 (댓글 또는 메모가 존재하지 않음)");
    }

    private Long updateLikesOfCommentsTable(Long commentId, boolean isReComment, boolean isCancel) {
        Long currentLikes = getLikesOfComment(commentId, isReComment); // 현재 좋아요 수
        String sql = isReComment ?
                "update post.recomment set likes = ? where id = ?"
                : "update post.comment set likes = ? where id = ?";

        Long updatedLikes = isCancel ? currentLikes - 1 : currentLikes + 1;
        template.update(sql, updatedLikes, commentId);

        return updatedLikes;
    }

    private void insertLikeOfMemberLikeCommentsTable(Long commentId, boolean isReComment,Long userId) {
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

    private void deleteLikeOfMemberLikeCommentsTable(Long commentId, boolean isReComment,Long userId) {
        String sql = "delete from member.like_comment where comment_id = ? and user_id = ? and is_recomment = ?";
        try {
            template.update(sql, commentId, userId, isReComment);
        }catch(Exception e){
            throw new CreateFailException("댓글 좋아요 취소에 실패하였습니다.");
        }

    }


    private Optional<CommentListDto> findParentCommentById(Long commentId){
        String sql = "select id,author_id,author_image_path, author_name, author_rank, likes, recomments, comment_text, created_date, updated_date, 'false' as is_liked " +
                "from post.comment " +
                "where id = ?";
        try {
            return Optional.of(template.queryForObject(sql, commentListRowMapper(), commentId));
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    private Long getLikesOfComment(Long commentId,boolean isReComment){
        String sql = "";
        if (isReComment) sql = "select likes from post.recomment where id = ?";
        else sql ="select likes from post.comment where id = ?";
        try{
            return template.queryForObject(sql, Long.class, commentId);
        }catch(EmptyResultDataAccessException e){
            throw new NotFoundException("좋아요한 댓글을 찾을 수 없습니다.");
        }
    }
    @Override
    public Long findAuthorIdByCommentId(Long commentId, Boolean isReComment){
        String sql = "";
        if (isReComment) sql = "select author_id from post.recomment where id = ?";
        else sql ="select author_id from post.comment where id =?";
        try {
            return template.queryForObject(sql, Long.class, commentId);
        }catch(EmptyResultDataAccessException e){
            throw new NotFoundException("comment not found",e);
        }
    }

    private RowMapper<PostIdAndTypeInfo> postIdAndPostTypeRowMapper() {
        return ((rs,rowNum)->
        {
            PostIdAndTypeInfo postIdAndTypeInfo = new PostIdAndTypeInfo(
                    rs.getLong("post_id"),
                    PostType.valueOf(rs.getString("post_type"))
            );
            return postIdAndTypeInfo;
        }

        );
    };
    private RowMapper<CommentListDto> commentListRowMapper() {
        return ((rs,rowNum)->
                CommentListDto.builder()
                        .id(rs.getLong("id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .authorRank(rs.getString("author_rank"))
                        .commentText(rs.getString("comment_text"))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .isLike(rs.getBoolean("is_liked"))
                        .likes(rs.getLong("likes"))
                        .reCommentsNumber(rs.getLong("recomments"))
                        .build());
    };

    private class PostInfo {
        private final Long postId;
        private final PostType postType;

        public PostInfo(Long postId, PostType postType) {
            this.postId = postId;
            this.postType = postType;
        }

        private Long getPostId() {
            return postId;
        }

        private PostType getPostType() {
            return postType;
        }
    }
    private RowMapper<PostInfo> postInfoRowMapper() {
        return ((rs,rowNum) -> new PostInfo(rs.getLong("post_id"), PostType.valueOf(rs.getString("post_type"))));
    };
    private RowMapper<Comment> commentRowMapper() {
        return ((rs,rowNum)->
                Comment.builder()
                        .id(rs.getLong("id"))
                        .postId(rs.getLong("post_id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .postTypeWithComment(PostType.valueOf(rs.getString("post_type")))
                        .commentText(rs.getString("comment_text"))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .likes(rs.getLong("likes"))
                        .rank(rs.getString("author_rank"))
                        .isUserDelete(rs.getBoolean("is_user_delete"))
                        .build());
    };
    private RowMapper<ReComment> reCommentRowMapper() {
        return ((rs,rowNum)->
                ReComment.builder()
                        .id(rs.getLong("id"))
                        .parentCommentId(rs.getLong("parent_id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .rank(rs.getString("author_rank"))
                        .commentText(rs.getString("comment_text"))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .likes(rs.getLong("likes"))
                        .build());
    };
    private RowMapper<ReCommentListDto> reCommentListRowMapper() {
        return ((rs,rowNum)->
                ReCommentListDto.builder()
                        .id(rs.getLong("id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .authorRank(rs.getString("author_rank"))
                        .commentText(rs.getString("comment_text"))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .likes(rs.getLong("likes"))
                        .isLike(rs.getBoolean("is_liked"))
                        .build());
    };

}
