package Funssion.Inforum.domain.post.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.score.Rank;
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

    @Override
    public void updateRankOfAllPostTypeAndNotification(Rank updateRank, Long userId) {
        updateAuthorRankOfAllPostType(updateRank, userId);
        updateSenderRankOfNotification(updateRank, userId);
    }

    private void updateAuthorRankOfAllPostType(Rank updateRank, Long userId) {
        for (PostType postType : PostType.values()) {
            String sql = "UPDATE post." + postType.getValue() +
                    " SET author_rank = ?" +
                    " WHERE author_id = ?";
            template.update(sql, updateRank.toString(), userId);
        }
    }

    private void updateSenderRankOfNotification(Rank updateRank, Long userId) {
        String sql = "UPDATE member.notification" +
                " SET sender_rank = ?" +
                " WHERE sender_id = ?";
        template.update(sql, updateRank.toString(), userId);
    }
}
