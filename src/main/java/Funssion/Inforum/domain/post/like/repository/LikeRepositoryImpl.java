package Funssion.Inforum.domain.post.like.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.post.like.domain.Like;
import Funssion.Inforum.domain.post.like.exception.LikeNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;

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
    public Like findById(Long id) {
        String sql = "select * from member.like where id = ?";

        return template.query(sql,
                        likeRowMapper(), id).stream().findAny()
                .orElseThrow(() -> new LikeNotFoundException());
    }

    @Override
    public Like findByUserIdAndPostInfo(Long userId, PostType postType, Long postId) {
        String sql = "select * from member.like where user_id = ? and post_type = ? and post_id = ?";

        return template.query(sql, likeRowMapper(), userId, postType.toString(), postId).stream().findAny()
                .orElseThrow(() -> new LikeNotFoundException());
    }

    @Override
    public List<Like> findAllByUserIdAndPostType(Long userId, PostType postType) {
        String sql = "select * from member.like where user_id = ? and post_type = ?";

        return template.query(sql, likeRowMapper(), userId, postType.toString());
    }

    @Override
    public void delete(Long userId, PostType postType, Long postId) {
        String sql = "delete from member.like where user_id = ? and post_type = ? and post_id = ?";

        if (template.update(sql, userId, postType.toString(), postId) == 0)
            throw new LikeNotFoundException();
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
}
