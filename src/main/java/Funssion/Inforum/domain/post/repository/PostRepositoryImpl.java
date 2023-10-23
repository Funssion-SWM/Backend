package Funssion.Inforum.domain.post.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.domain.mypage.dto.ScoreAndCount;
import Funssion.Inforum.domain.mypage.domain.ScoreAndCountDao;
import Funssion.Inforum.domain.mypage.domain.ScoreAndCountDao.ScoreAndCountDaoBuilder;
import Funssion.Inforum.domain.score.Rank;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    @Override
    public boolean isRankUpdateAllPost(Rank updatedRank, Long userId) {
        for (PostType postType : PostType.values()) {
            String sql = "SELECT author_rank" +
                    " FROM post." + postType.getValue() +
                    " WHERE author_id = ?";
            try {
                if (!template.queryForObject(sql, String.class, userId).equals(updatedRank.toString())) {
                    return false;
                }
            }catch(EmptyResultDataAccessException e){}

        }
        return true;
    }

    @Override
    public ScoreAndCountDao getAllPostScoreAndCount(Long userId) {
        return getScoreAndCountOfEachScoreType(userId);
    }

//
    private ScoreAndCountDao getScoreAndCountOfEachScoreType(Long userId) {
        ScoreAndCountDaoBuilder addScoreAndCountInfo = ScoreAndCountDao.builder();
        String sql ="";
        for (ScoreType scoreType : ScoreType.values()) {
            if (scoreType == ScoreType.LIKE) {
                sql = "SELECT sum(score) as score, count(*) as count " +
                        "FROM score.info " +
                        "WHERE liked_author_id = ?";
                ScoreAndCount scoreAndCount = template.queryForObject(sql, sumOfScoreAndCountRowMapper(), userId);
                addScoreAndCountInfo = addScoreAndCountInfo.likeScoreAndCount(scoreAndCount);
                continue;
            }
            sql = "SELECT sum(score) as score, count(*) as count " +
                    "FROM score.info " +
                    "WHERE user_id = ? and score_type = ?";
            ScoreAndCount scoreAndCount = template.queryForObject(sql, sumOfScoreAndCountRowMapper(), userId,scoreType.toString());
            addScoreAndCountBy(scoreType,addScoreAndCountInfo,scoreAndCount);
        }
        return addScoreAndCountInfo.build();
    }

    public void addScoreAndCountBy(ScoreType scoreType, ScoreAndCountDaoBuilder addScoreAndCountInfo, ScoreAndCount scoreAndCount){
        switch(scoreType){
            case MAKE_ANSWER ->  addScoreAndCountInfo.answerScoreAndCount(scoreAndCount);
            case MAKE_QUESTION -> addScoreAndCountInfo.questionScoreAndCount(scoreAndCount);
            case MAKE_COMMENT -> addScoreAndCountInfo.commentScoreAndCount(scoreAndCount);
            case MAKE_MEMO -> addScoreAndCountInfo.memoScoreAndCount(scoreAndCount);
            case SELECT_ANSWER -> addScoreAndCountInfo.selectingAnswerScoreAndCount(scoreAndCount);
            case BEST_ANSWER -> addScoreAndCountInfo.bestAnswerScoreAndCount(scoreAndCount);
            default -> throw new BadRequestException("SERIES 포스트 타입은 활동 내역에 포함하지 않습니다.");
        }
    }
    private RowMapper<ScoreAndCount> sumOfScoreAndCountRowMapper() {
        return (rs, rowNum) -> new ScoreAndCount(rs.getLong("score"),rs.getLong("count"));
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
