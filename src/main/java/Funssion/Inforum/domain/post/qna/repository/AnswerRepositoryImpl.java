package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.domain.post.qna.exception.AnswerNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public List<Answer> getAnswersOfQuestion(Long questionId) {
        String sql = "select id, question_id, author_id, author_name, author_image_path, question_id, text, likes, created_date, updated_date, is_selected, replies_count"
                +" from question.answer where question_id = ?";
        return template.query(sql,answerRowMapper(),questionId);
    }

    @Override
    public Long getAuthorIdOf(Long answerId) {
        String sql = "select author_id from question.answer where id = ?";
        try {
            return template.queryForObject(sql, Long.class, answerId);
        }catch(EmptyResultDataAccessException e){
            throw new AnswerNotFoundException("존재하지 않은 답변 글입니다.");
        }
    }

    @Override
    public Answer updateAnswer(AnswerSaveDto answerSaveDto, Long answerId) {
        String sql = "update question.answer set text = ?::jsonb, updated_date = ? where id = ?";

        if(template.update(sql, answerSaveDto.getText(), LocalDateTime.now(), answerId)==0){
            throw new AnswerNotFoundException("update answer fail");
        }

        return this.getAnswerById(answerId);
    }

    public Answer getAnswerById(Long id){
        String sql = "select id, question_id, author_id, author_name, author_image_path, question_id, text, likes, created_date, updated_date, is_selected, replies_count"
                + " from question.answer where id = ?";
        try{
            return template.queryForObject(sql,answerRowMapper(),id);
        }catch(EmptyResultDataAccessException e){
            throw new AnswerNotFoundException("해당 답변 글을 찾을 수 없습니다.");
        }
    }

    @Override
    public void deleteAnswer(Long answerId) {
        String sql = "delete from question.answer where id = ?";
        if(template.update(sql,answerId)==0){
            throw new AnswerNotFoundException("삭제할 답변이 존재하지 않습니다.");
        }
    }

    private RowMapper<Answer> answerRowMapper() {
        return ((rs, rowNum) ->
                Answer.builder()
                        .id(rs.getLong("id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .questionId(rs.getLong("question_id"))
                        .text(rs.getString("text"))
                        .likes(rs.getLong("likes"))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .repliesCount(rs.getLong("replies_count"))
                        .isSelected(rs.getBoolean("is_selected"))
                        .build());
    }
}
