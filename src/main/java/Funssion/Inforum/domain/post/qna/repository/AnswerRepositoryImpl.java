package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.Sign;
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

        String sql = "insert into post.answer(question_id,author_id, author_name, author_image_path, text,author_rank) " +
                "values(?,?,?,?,?::jsonb,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con->{
            PreparedStatement psmt = con.prepareStatement(sql,new String[]{"id"});
            psmt.setLong(1,answer.getQuestionId());
            psmt.setLong(2, answer.getAuthorId());
            psmt.setString(3, answer.getAuthorName());
            psmt.setString(4, answer.getAuthorImagePath());
            psmt.setString(5, answer.getText());
            psmt.setString(6,answer.getRank());
            return psmt;
        },keyHolder);
        return this.getAnswerById(keyHolder.getKey().longValue());
    }

    @Override
    public List<Answer> getAnswersOfQuestion(Long loginId, Long questionId) {
        String sql =
                 "select A.id, A.question_id, A.author_id, A.author_name, A.author_image_path, A.question_id, A.author_rank, A.text, A.dislikes, A.likes, A.created_date, A.updated_date, A.is_selected, A.replies_count,"
                +" case when L.post_id = A.id then true else false end as is_like, "
                +" case when DL.post_id = A.id then true else false end as is_dislike"
                +" FROM (SELECT id, question_id, author_id, author_name, author_image_path, author_rank, text, dislikes, likes, created_date, updated_date, is_selected, replies_count"
                        +" FROM post.answer WHERE question_id = ?) AS A"
                        +" left join (select post_id from member.dislike where user_id = ? and post_type = '"+PostType.ANSWER+"') AS DL"
                        +" on A.id = DL.post_id "
                        +" left join (select post_id from member.like where user_id = ? and post_type = '"+ PostType.ANSWER+"') AS L"
                        +" on A.id = L.post_id"
                        +" order by A.is_selected desc, (A.likes - A.dislikes) desc, created_date desc";
        return template.query(sql,answerLikeRowMapper(), questionId, loginId, loginId);
    }

    @Override
    public Long getAuthorIdOf(Long answerId) {
        String sql = "select author_id from post.answer where id = ?";
        try {
            return template.queryForObject(sql, Long.class, answerId);
        }catch(EmptyResultDataAccessException e){
            throw new AnswerNotFoundException("존재하지 않은 답변 글입니다.");
        }
    }

    @Override
    public Answer updateAnswer(AnswerSaveDto answerSaveDto, Long answerId) {
        String sql = "update post.answer set text = ?::jsonb, updated_date = ? where id = ?";

        if(template.update(sql, answerSaveDto.getText(), LocalDateTime.now(), answerId)==0){
            throw new AnswerNotFoundException("update answer fail");
        }

        return this.getAnswerById(answerId);
    }

    public Answer getAnswerById(Long id){
        String sql = "select id, question_id, author_id, author_name, author_image_path, author_rank, question_id, dislikes, text, likes, created_date, updated_date, is_selected, replies_count"
                + " from post.answer where id = ?";
        try{
            return template.queryForObject(sql,answerRowMapper(),id);
        }catch(EmptyResultDataAccessException e){
            throw new AnswerNotFoundException("해당 답변 글을 찾을 수 없습니다.");
        }
    }

    @Override
    public void updateAnswersCountOfQuestion(Long questionId, Sign sign) {
        String sql = "";
        switch(sign){
            case PLUS -> sql = "update post.question set answers = answers + 1 where id = ?";
            case MINUS -> sql = "update post.question set answers = answers - 1 where id = ?";
        }
        if (template.update(sql,questionId) == 0) throw new AnswerNotFoundException("update likes fail");
    }

    @Override
    public Answer updateLikesInAnswer(Long likes, Long answerId) {
        String sql = "update post.answer " +
                "set likes = ? " +
                "where id = ?";

        if (template.update(sql, likes, answerId) == 0) throw new AnswerNotFoundException("update likes fail");
        return getAnswerById(answerId);
    }

    @Override
    public Answer updateDisLikesInAnswer(Long disLikes, Long answerId) {
        String sql = "update post.answer " +
                "set dislikes = ? " +
                "where id = ?";

        if (template.update(sql, disLikes, answerId) == 0) throw new AnswerNotFoundException("update likes fail");
        return getAnswerById(answerId);
    }

    @Override
    public void deleteAnswer(Long answerId) {
        String sql = "delete from post.answer where id = ?";
        if(template.update(sql,answerId)==0){
            throw new AnswerNotFoundException("삭제할 답변이 존재하지 않습니다.");
        }
    }

    @Override
    public Answer select(Long answerId) {
        String sql = "update post.answer set is_selected = true where id = ?";
        if(template.update(sql,answerId)==0){
            throw new AnswerNotFoundException("삭제할 답변이 존재하지 않습니다.");
        }
        return getAnswerById(answerId);
    }

    @Override
    public void updateProfileImage(Long userId, String profileImageFilePath) {
        String sql = "update post.answer " +
                "set author_image_path = ? " +
                "where author_id = ?";

        template.update(sql, profileImageFilePath, userId);
    }

    private RowMapper<Answer> answerRowMapper() {
        return ((rs, rowNum) ->
                Answer.builder()
                        .id(rs.getLong("id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .rank(rs.getString("author_rank"))
                        .questionId(rs.getLong("question_id"))
                        .text(rs.getString("text"))
                        .likes(rs.getLong("likes"))
                        .dislikes(rs.getLong("dislikes"))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .repliesCount(rs.getLong("replies_count"))
                        .isSelected(rs.getBoolean("is_selected"))
                        .build());
    }private RowMapper<Answer> answerLikeRowMapper() {
        return ((rs, rowNum) ->
                Answer.builder()
                        .id(rs.getLong("id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .rank(rs.getString("author_rank"))
                        .questionId(rs.getLong("question_id"))
                        .text(rs.getString("text"))
                        .likes(rs.getLong("likes"))
                        .isLike(rs.getBoolean("is_like"))
                        .isDisLike(rs.getBoolean("is_dislike"))
                        .dislikes(rs.getLong("dislikes"))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .repliesCount(rs.getLong("replies_count"))
                        .isSelected(rs.getBoolean("is_selected"))
                        .build());
    }
}
