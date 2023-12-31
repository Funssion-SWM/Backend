package Funssion.Inforum.domain.score.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.common.exception.etc.UpdateFailException;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.score.Rank;
import Funssion.Inforum.domain.score.domain.Score;
import Funssion.Inforum.domain.score.dto.ScoreRank;
import Funssion.Inforum.domain.score.dto.UserInfoWithScoreRank;
import Funssion.Inforum.domain.score.exception.ScoreUpdateFailException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
public class ScoreRepository {
    private final JdbcTemplate template;
    public ScoreRepository(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }
    public Long updateUserScoreAtDay(Long userId, Long addScore, Long updateDailyScore){
        String sql = "update member.info set score = score + ?, daily_get_score = ? where id = ?";
        if(template.update(sql,addScore, updateDailyScore, userId) == 0) throw new ScoreUpdateFailException("점수를 Update할 유저가 존재하지 않습니다.");
        return getScoreAndRank(userId).getScore();
    }
    public Long updateUserScoreAtOtherDay(Long userId, Long minusScore){
        String sql = "update member.info set score = score + ? where id = ?";
        if(template.update(sql,minusScore, userId) == 0) throw new ScoreUpdateFailException("점수를 Update할 유저가 존재하지 않습니다.");
        return getScoreAndRank(userId).getScore();
    }

    public Long getUserDailyScore(Long userId){
        String sql = "select daily_get_score from member.info where id = ?";
        return template.queryForObject(sql, Long.class, userId);
    }

    public Optional<Score> saveScoreHistory(Long userId, ScoreType scoreType, Long score, Long postId){
        String postType = switch(scoreType){
            case MAKE_MEMO -> "MEMO";
            case BEST_ANSWER, MAKE_ANSWER -> "ANSWER";
            case MAKE_QUESTION, SELECT_ANSWER -> "QUESTION";
            case MAKE_COMMENT -> "COMMENT";
            default -> throw new IllegalStateException("Unexpected value: " + scoreType);
        };
        String sql = "insert into score.info(user_id,score_type,score, post_type, post_id) values (?,?,?,?,?)";
        if(template.update(sql,userId,scoreType.toString(),score,postType,postId)==0) throw new ScoreUpdateFailException("Score History를 저장할 수 없습니다.");
        return findScoreHistoryInfoById(userId,scoreType,postId);
    }
    public Optional<Score> saveScoreHistory(Long userId, ScoreType scoreType, Long score, Long postId, PostType postType, Long likedAuthorId){
        String sql = "insert into score.info(user_id,score_type,score,post_id,post_type,liked_author_id) values (?,?,?,?,?,?)";
        if(template.update(sql,userId,scoreType.toString(),score,postId,postType.toString(),likedAuthorId)==0) throw new ScoreUpdateFailException("Score History를 저장할 수 없습니다.");
        return findScoreHistoryInfoById(userId,scoreType,postId);
    }

    public ScoreRank getScoreAndRank(Long userId) throws EmptyResultDataAccessException {
        String sql = "select score,rank,daily_get_score from member.info where id = ?";
        try {
            return template.queryForObject(sql, scoreRankRowMapper(), userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UnAuthorizedException("해당 유저가 존재하지 않습니다.");
        }
    }

    private RowMapper<ScoreRank> scoreRankRowMapper(){
        return (rs, rowNum) -> new ScoreRank(rs.getLong("score"),Rank.valueOf(rs.getString("rank")),rs.getLong("daily_get_score"));
    }
    /**
     * score history는 점수를 '획득' 했을 때에만 저장합니다. 이에 유의하여 삭제 api를 작성합니다.
     */

    public void deleteScoreHistory(Score score){
        String sql = "delete from score.info where user_id = ? and score_type = ? and post_id = ?";
        if(template.update(sql, score.getUserId(), score.getScoreType().toString(), score.getPostId())==0) throw new HistoryNotFoundException("삭제할 score 정보가 존재하지 않습니다.");
    }

    /**
     * 일일 최대 점수 획득치가 넘어가는 동작의 history는 table에 존재하지 않으므로, 해당 경우에 Null로 처리합니다.
     * PK는 복합 키 (user_id,score_type,post_id) 입니다. 즉 한 유저가, 같은 '행동 타입'을 '한가지 포스트'에만 할 수 있습니다.
     */
    public Optional<Score> findScoreHistoryInfoById(Long userId, ScoreType scoreType, Long postId){
        String sql = "select user_id,score_type,score,post_id,created_date from score.info where user_id = ? and score_type = ? and post_id = ?";
        try {
            return Optional.ofNullable(template.queryForObject(sql, scoreHistoryRowMapper(), userId,scoreType.toString(),postId));
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }



    public void initializeAllUsersDailyScore() {
        String sql = "update member.info set daily_get_score = 0";
        template.update(sql);
    }

    public String getRank(Long userId) {
        String sql = "select rank from member.info where id = ?";
        return template.queryForObject(sql,String.class,userId);
    }

    public Rank updateRank(Rank beUpdateRank, Long userId) {
        String sql = "update member.info set rank = ? where id = ?";
        if(template.update(sql,beUpdateRank.toString(),userId)==0) throw new UpdateFailException("rank가 변경되어야 하지만 변경되지 않았습니다.");
        return beUpdateRank;
    }

    public List<UserInfoWithScoreRank> getTopTenUsers() {
        String sql = "SELECT id, name, image_path, score, rank, RANK() over " +
                "(ORDER BY score DESC) ranking "+
                "FROM member.info " +
                "WHERE is_deleted = false " +
                "LIMIT 10";
        return template.query(sql,userInfoWithScoreRankRowMapper());
    }

    private RowMapper<Score> scoreHistoryRowMapper(){
        return new RowMapper<Score>() {
            @Override
            public Score mapRow(ResultSet rs, int rowNum) throws SQLException {
                Score score = Score.builder()
                        .userId(rs.getLong("user_id"))
                        .scoreType(ScoreType.valueOf(rs.getString("score_type")))
                        .score(rs.getLong("score"))
                        .postId(rs.getLong("post_id"))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .build();
                return score;
            }
        };
    }
    private RowMapper<UserInfoWithScoreRank> userInfoWithScoreRankRowMapper(){
        return (rs,rowNum) ->{
            return UserInfoWithScoreRank.builder()
                        .memberProfileEntity(MemberProfileEntity.builder()
                                .profileImageFilePath(rs.getString("image_path"))
                                .nickname(rs.getString("name"))
                                .userId(rs.getLong("id"))
                                .build())
                        .scoreRank(ScoreRank.builder().score(rs.getLong("score"))
                                        .rank(Rank.valueOf(rs.getString("rank"))).build())
                        .ranking(rs.getLong("ranking"))
                    .build();
        };
    }


    public UserInfoWithScoreRank getMyRank(Long userId) {

        String sql = "SELECT id, name, image_path, score, rank, R.ranking FROM(" +
                        "SELECT id, name, image_path, score, rank, RANK() over " +
                        "(ORDER BY score DESC) ranking "+
                        "FROM member.info " +
                        "WHERE is_deleted = false) R " +
                    "WHERE id = ?";
        return template.queryForObject(sql,userInfoWithScoreRankRowMapper(),userId);
    }
}
