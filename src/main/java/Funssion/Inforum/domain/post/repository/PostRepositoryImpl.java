package Funssion.Inforum.domain.post.repository;

import Funssion.Inforum.common.constant.PostType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final JdbcTemplate template;

    public PostRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Long findAuthorId(PostType postType, Long postId) {
        String sql = "select author_id from post."+ postType.getValue() +" where id = ?";

        return template.queryForObject(sql, Long.class, postId);
    }
}
