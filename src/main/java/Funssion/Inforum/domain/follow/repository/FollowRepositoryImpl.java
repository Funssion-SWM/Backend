package Funssion.Inforum.domain.follow.repository;

import Funssion.Inforum.domain.follow.domain.Follow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class FollowRepositoryImpl implements FollowRepository {

    private final JdbcTemplate template;

    public FollowRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public void save(Follow follow) {
        String sql = "insert into member.follow (user_id, follow_id) " +
                "values(?, ?)";

        template.update(sql, follow.getUserId(), follow.getFollowId());
    }

    @Override
    public Optional<Follow> findByUserIdAndFollowId(Long userId, Long followId) {
        String sql = "select * from member.follow where user_id = ? and follow_id = ?";

        return template.query(sql, followRowMapper(), userId, followId).stream().findAny();
    }

    private RowMapper<Follow> followRowMapper() {
        return (rs, rowNum) -> Follow.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .followId(rs.getLong("follow_id"))
                .created(rs.getTimestamp("created").toLocalDateTime())
                .build();
    }
}
