package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


@Repository
public class ScoreRepository {
    private final JdbcTemplate template;
    public ScoreRepository(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }
    public Long updateUserScoreAtDay(Long userId, Long addScore, Long updateDailyScore){
        String sql = "update member.info set score = score + ?, daily_get_score = ? where user_id = ?";
        if(template.update(sql,addScore, updateDailyScore, userId) == 0) throw new ScoreUpdateFailException("점수를 Update할 유저가 존재하지 않습니다.");
        return addScore;
    }
    public Long updateUserScoreAtOtherDay(Long userId, Long minusScore){
        String sql = "update member.info set score = score - ? where user_id = ?";
        if(template.update(sql,minusScore, userId) == 0) throw new ScoreUpdateFailException("점수를 Update할 유저가 존재하지 않습니다.");
        return minusScore;
    }

    public Long getUserDailyScore(Long userId){
        String sql = "select daily_get_score from member.info where user_id = ?";
        return template.queryForObject(sql, Long.class, userId);
    }

    public Long saveScoreHistory(Long userId, ScoreType scoreType, Long score,Long postId){
        String sql = "insert into score.info(user_id,score_type,score,post_id) values (?,?,?,?)";
        KeyHolder scoreKeyHolder = new GeneratedKeyHolder();
        template.update(con->{
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
            psmt.setLong(1,userId);
            psmt.setString(2,scoreType.name());
            psmt.setLong(3,score);
            psmt.setLong(4,postId);
            return psmt;
        },scoreKeyHolder);
        return scoreKeyHolder.getKey().longValue();
    }

    /**
     * score history는 점수를 '획득' 했을 때에만 저장합니다. 이에 유의하여 삭제 api를 작성합니다.
     */

    public void deleteScoreHistory(Long userId, ScoreType scoreType, Long postId){
        String sql = "delete from score.info where user_id = ? and score_type = ? and post_id = ?";
        if(template.update(sql, userId, scoreType, postId)==0) throw new HistoryNotFoundException("삭제할 score 정보가 존재하지 않습니다.");
    }

    /**
     * 일일 최대 점수 획득치가 넘어가는 동작의 history는 table에 존재하지 않으므로, 해당 경우에 Null로 처리합니다.
     * PK는 복합 키 (user_id,score_type,post_id) 입니다. 즉 한 유저가, 같은 '행동 타입'을 '한가지 포스트'에만 할 수 있습니다.
     */
    public Optional<Score> findScoreHistoryInfoById(Long userId, ScoreType scoreType, Long postId){
        String sql = "select id,user_id,score_type,score,post_id,created_date from score.info where user_id = ?, score_type = ?, post_id = ?";
        try {
            return Optional.ofNullable(template.queryForObject(sql, scoreHistoryRowMapper(), userId,scoreType,postId));
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    private RowMapper<Score> scoreHistoryRowMapper(){
        return new RowMapper<Score>() {
            @Override
            public Score mapRow(ResultSet rs, int rowNum) throws SQLException {
                Score score = Score.builder()
                        .id(rs.getLong("id"))
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


    public void initializeAllUsersDailyScore() {
        String sql = "update member.info set daily_get_score = 0";
        template.update(sql);
    }
}
