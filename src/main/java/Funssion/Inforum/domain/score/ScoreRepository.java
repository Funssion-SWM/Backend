package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ScoreRepository {
    private final JdbcTemplate template;
    public ScoreRepository(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }

    public Score saveScoreHistory(Long userId, ScoreType scoreType, Long postId){

        String sql = "insert into score.info(user_id,score_type,post_id) values (?,?,?)";
        KeyHolder scoreKeyHolder = new GeneratedKeyHolder();
        template.update(con->{
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
            psmt.setLong(1,userId);
            psmt.setString(2,scoreType.name());
            psmt.setLong(3,postId);
            return psmt;
        },scoreKeyHolder);
        long keyOfScore = scoreKeyHolder.getKey().longValue();
        return findScoreInfoById(keyOfScore);
    }

    public void deleteScoreHistory(Long userId, ScoreType scoreType, Long postId){
        String sql = "delete from score.info where user_id = ? and score_type = ? and post_id = ?";
        if(template.update(sql, userId, scoreType, postId)==0) throw new HistoryNotFoundException("삭제할 score 정보가 존재하지 않습니다.");

    }




    private Score findScoreInfoById(Long id){
        String sql = "select id,user_id,score_type,post_id,created_date from score.info where id = ?";
        return template.queryForObject(sql,scoreHistoryRowMapper(), id);
    }

    private RowMapper<Score> scoreHistoryRowMapper(){
        return new RowMapper<Score>() {
            @Override
            public Score mapRow(ResultSet rs, int rowNum) throws SQLException {
                Score score = Score.builder()
                        .id(rs.getLong("id"))
                        .userId(rs.getLong("user_id"))
                        .scoreType(ScoreType.valueOf(rs.getString("score_type")))
                        .postId(rs.getLong("post_id"))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .build();
                return score;
            }
        };
    }


}
