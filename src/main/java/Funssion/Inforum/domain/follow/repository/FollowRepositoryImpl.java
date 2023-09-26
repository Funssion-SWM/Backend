package Funssion.Inforum.domain.follow.repository;

import Funssion.Inforum.domain.follow.controller.domain.Follow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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
}
