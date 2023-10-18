package Funssion.Inforum.domain.post.like.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.post.like.domain.DisLike;
import Funssion.Inforum.domain.post.like.domain.Like;
import Funssion.Inforum.domain.post.like.exception.DisLikeNotFoundException;
import Funssion.Inforum.domain.post.like.exception.LikeNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class LikeRepositoryImpl implements LikeRepository {

    private final JdbcTemplate template;

    public LikeRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Like create(Like like) {
        String sql = "insert into member.like (user_id, post_type, post_id) values (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
            psmt.setLong(1, like.getUserId());
            psmt.setString(2, like.getPostType().toString());
            psmt.setLong(3, like.getPostId());
            return psmt;
        }, keyHolder);

        return findById(keyHolder.getKey().longValue());
    }

    @Override
    public DisLike createDisLike(DisLike disLike) {
        String sql = "insert into member.dislike (user_id, post_type, post_id) values (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
            psmt.setLong(1, disLike.getUserId());
            psmt.setString(2, disLike.getPostType().toString());
            psmt.setLong(3, disLike.getPostId());
            return psmt;
        }, keyHolder);

        return findByIdOfDisLike(keyHolder.getKey().longValue());
    }


    @Override
    public Like findById(Long id) {
        String sql = "select * from member.like where id = ?";

        return template.query(sql,
                    likeRowMapper(), id).stream().findAny()
                .orElseThrow(() -> new LikeNotFoundException());
    }

    @Override
    public DisLike findByIdOfDisLike(Long id) {
        String sql = "select * from member.dislike where id = ?";

        return template.query(sql,
                        disLikeRowMapper(), id).stream().findAny()
                    .orElseThrow(() -> new DisLikeNotFoundException());
    }


    @Override
    public Optional<Like> findByUserIdAndPostInfo(Long userId, PostType postType, Long postId) {
        String sql = "select * from member.like where user_id = ? and post_type = ? and post_id = ?";

        return template.query(sql, likeRowMapper(), userId, postType.toString(), postId)
                .stream()
                .findAny();
    }

    @Override
    public List<Like> findAllByUserIdAndPostType(Long userId, PostType postType) {
        String sql = "select * from member.like where user_id = ? and post_type = ?";

        return template.query(sql, likeRowMapper(), userId, postType.toString());
    }

    @Override
    public Optional<DisLike> findByUserIdAndPostInfoOfDisLike(Long userId, PostType postType, Long postId) {
        String sql = "select * from member.dislike where user_id = ? and post_type = ? and post_id = ?";

        return template.query(sql, disLikeRowMapper(), userId, postType.toString(), postId)
                .stream()
                .findAny();
    }


    @Override
    public void deleteLike(Long userId, PostType postType, Long postId) {
        String sql = "delete from member.like where user_id = ? and post_type = ? and post_id = ?";

        if (template.update(sql, userId, postType.toString(), postId) == 0)
            throw new LikeNotFoundException();
    }

    @Override
    public void deleteDisLike(Long userId, PostType postType, Long postId) {
        String sql = "delete from member.dislike where user_id = ? and post_type = ? and post_id = ?";

        if (template.update(sql, userId, postType.toString(), postId) == 0)
            throw new DisLikeNotFoundException();
    }

    @Override
    public Integer howManyLikesInPost(PostType postType, Long postId) {
        String sql = "select count(id) from member.like where post_type = ? and post_id = ?";
        return template.queryForObject(sql,Integer.class,postType.toString(),postId);
    }

    private RowMapper<Like> likeRowMapper() {
        return ((rs, rowNum) -> Like.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .postType(PostType.valueOf(rs.getString("post_type")))
                .postId(rs.getLong("post_id"))
                .created(rs.getTimestamp("created"))
                .build());
    }
    private RowMapper<DisLike> disLikeRowMapper() {
        return ((rs, rowNum) -> DisLike.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .postType(PostType.valueOf(rs.getString("post_type")))
                .postId(rs.getLong("post_id"))
                .created(rs.getTimestamp("created"))
                .build());
    }
}
