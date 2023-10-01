package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.exception.AnswerNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;

@Repository
public class AnswerRepositoryImpl implements AnswerRepository {
    private final JdbcTemplate template;
    public AnswerRepositoryImpl(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Answer createAnswer(Answer answer) {

        String sql = "insert into question.answer(question_id,author_id, author_name, author_image_path, text) " +
                "values(?,?,?,?,?::jsonb)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con->{
            PreparedStatement psmt = con.prepareStatement(sql,new String[]{"id"});
            psmt.setLong(1,answer.getQuestionId());
            psmt.setLong(2, answer.getAuthorId());
            psmt.setString(3, answer.getAuthorName());
            psmt.setString(4, answer.getAuthorImagePath());
            psmt.setString(5, answer.getText());
            return psmt;
        },keyHolder);
        return this.getAnswerById(keyHolder.getKey().longValue());
    }
    public Answer getAnswerById(Long id){
        String sql = "select id, question_id, author_id, author_name, author_image_path, text, likes, created_date, updated_date, is_selected, replies_count"
                + " from question.answer where id = ?";
        try{
            return template.queryForObject(sql,answerRowMapper(),id);
        }catch(EmptyResultDataAccessException e){
            throw new AnswerNotFoundException("해당 답변 글을 찾을 수 없습니다.");
        }
    }
    private RowMapper<Answer> answerRowMapper() {
        return ((rs, rowNum) ->
                Answer.builder()
                        .id(rs.getLong("id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .text(rs.getString("text"))
                        .likes(rs.getLong("likes"))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .repliesCount(rs.getLong("replies_count"))
                        .isSelected(rs.getBoolean("is_selected"))
                        .build());
    }
}
