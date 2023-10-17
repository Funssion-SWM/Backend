package Funssion.Inforum.domain.post.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.etc.UpdateFailException;
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
    public void updateRankOfAllPostTypeAndNotification(Rank updateRank, Long postId) {
        int postCount = PostType.values().length + 1;
        postCount = updateAuthorRankOfAllPosType(updateRank, postId, postCount);
        postCount = updateSenderRankOfNotification(updateRank, postId, postCount);
        if(postCount !=0){
            throw new UpdateFailException("모든 포스트 타입, Notification에 rank가 업데이트 되지 않았습니다. 안된 수 = " + postCount);
        }
    }

    private int updateAuthorRankOfAllPosType(Rank updateRank, Long postId, int postCount) {
        for (PostType postType : PostType.values()) {
            String sql = "UPDATE post." + postType.getValue() +
                    " SET author_rank = ?" +
                    " WHERE id = ?";
            postCount -= template.update(sql, updateRank, postId);
        }
        return postCount;
    }

    private int updateSenderRankOfNotification(Rank updateRank, Long postId, int postCount) {
        String sql = "UPDATE member.notification" +
                " SET sender_rank = ?" +
                " WHERE sender_id = ?";
        postCount -= template.update(sql, updateRank, postId);
        return postCount;
    }
}
