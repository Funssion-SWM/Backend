package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.exception.etc.ArrayToListException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.exception.DuplicateSelectedAnswerException;
import Funssion.Inforum.domain.post.qna.exception.QuestionNotFoundException;
import Funssion.Inforum.domain.tag.TagUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class QuestionRepositoryImpl implements QuestionRepository {
    private final JdbcTemplate template;
    public QuestionRepositoryImpl(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Question createQuestion(Question question) {
        List<String> questionTags = question.getTags();

        String sql = "insert into question.info(author_id, author_name, author_image_path, title, text, tags, memo_id, description) " +
                "values(?,?,?,?,?::jsonb,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con->{
            PreparedStatement psmt = con.prepareStatement(sql,new String[]{"id"});
            psmt.setLong(1, question.getAuthorId());
            psmt.setString(2, question.getAuthorName());
            psmt.setString(3, question.getAuthorImagePath());
            psmt.setString(4, question.getTitle());
            psmt.setString(5, question.getText());
            psmt.setArray(6, TagUtils.createSqlArray(template,questionTags));
            psmt.setLong(7,question.getMemoId());
            psmt.setString(8,question.getDescription());
            return psmt;
        },keyHolder);
        return this.getOneQuestion(keyHolder.getKey().longValue());
    }

    @Override
    public Question updateQuestion(QuestionSaveDto questionSaveDto, Long questionId) {
        String sql = "update question.info " +
                "set title = ?, text = ?::jsonb, tags = ?, description = ? where id = ?";
        try {
            if(template.update(sql, questionSaveDto.getTitle(), questionSaveDto.getText(), TagUtils.createSqlArray(template,questionSaveDto.getTags()),questionSaveDto.getDescription(), questionId)==0){
                throw new QuestionNotFoundException("update question fail");
            }
        }catch (SQLException e){
            throw new ArrayToListException("Javax.sql.Array (PostgreSQL의 array) 를 List로 변경할 때의 오류",e);
        }
        return this.getOneQuestion(questionId);
    }

    @Override
    public List<Question> getQuestions(Long userId,OrderType orderBy) {
        String sql =
                "select Q.id, Q.author_id, Q.author_name, Q.author_image_path, Q.title, Q.text, Q.description, Q.likes, Q.is_solved, Q.created_date, Q.updated_date, Q.tags, Q.replies_count, Q.answers, Q.is_solved, Q.memo_id, "+
                "case when L.post_id = Q.id then true else false end as is_like "+
                "from question.info AS Q "+
                "left join (select post_id from member.like where user_id = ? and post_type = '"+ PostType.QUESTION+"') AS L "+
                        "on Q.id = L.post_id ";
        sql += getSortedSql(orderBy);
        return template.query(sql, questionLikeRowMapper(),userId);
    }

    @Override
    public Long getAuthorId(Long questionId) {
        String sql = "select author_id from question.info where id = ?";
        try{
            return template.queryForObject(sql, Long.class, questionId);
        }catch(EmptyResultDataAccessException e){
            throw new NotFoundException("수정할 질문 게시글이 이미 삭제되었습니다.",e);
        }
    }
    @Override
    public List<Question> getQuestionsOfMemo(Long userId,Long memoId) {
        String sql =
                "select Q.id, Q.author_id, Q.author_name, Q.author_image_path, Q.title, Q.text, Q.description, Q.likes, Q.is_solved, Q.created_date, Q.updated_date, Q.tags, Q.replies_count, Q.answers, Q.is_solved, Q.memo_id, "+
                "case when L.post_id = Q.id then true else false end as is_like "+
                "from (select id, author_id, author_name, author_image_path, title, text, description, likes, is_solved, created_date, updated_date, tags, replies_count, answers, memo_id " +
                        "from question.info where memo_id = ?) AS Q "+
                        "left join (select post_id from member.like where user_id = ? and post_type = '"+ PostType.QUESTION+"') AS L "+
                        "on Q.id = L.post_id";

        return template.query(sql,questionLikeRowMapper(),memoId, userId);
    }

    @Override
    public void updateProfileImage(Long userId, String profileImageFilePath) {
        String sql = "update question.info " +
                "set author_image_path = ? " +
                "where author_id = ?";

        template.update(sql, profileImageFilePath, userId);
    }

    @Override
    public Question getOneQuestion(Long questionId) {
        String sql = "select id, author_id, author_name, author_image_path, title, text, description, likes, is_solved, created_date, updated_date, tags, replies_count, answers, is_solved, memo_id " +
                "from question.info where id = ?";
        try{
            return template.queryForObject(sql,questionRowMapper(),questionId);
        }catch(EmptyResultDataAccessException e){
            throw new QuestionNotFoundException("해당 질문 글을 찾을 수 없습니다.");
        }
    }

    @Override
    public void deleteQuestion(Long questionId) {
        String sql = "delete from question.info where id = ?";
        if (template.update(sql,questionId)==0) throw new QuestionNotFoundException("삭제할 질문이 존재하지 않습니다.");
    }

    @Override
    public void solveQuestion(Long questionId) {
        String sql = "update question.info set is_solved = true where id = ?";
        exceptionHandlingOfNonUniqueSolved(questionId);
        if (template.update(sql,questionId)==0) throw new QuestionNotFoundException("삭제할 질문이 존재하지 않습니다.");
    }

    private void exceptionHandlingOfNonUniqueSolved(Long questionId){
        String sql = "select count(id) from question.info where is_solved is true and id = ?";
        Long selectedAnswerCount = template.queryForObject(sql, Long.class, questionId);
        if (selectedAnswerCount !=0){
            throw new DuplicateSelectedAnswerException("두개의 답변을 채택할 순 없습니다.");
        }
    }
    @Override
    public Question updateLikesInQuestion(Long likes, Long questionId) {
        String sql = "update question.info " +
                "set likes = ? " +
                "where id = ?";

        if (template.update(sql, likes, questionId) == 0) throw new QuestionNotFoundException("update likes fail");
        return getOneQuestion(questionId);
    }


    private String getSortedSql(OrderType orderBy){
        switch (orderBy){
            case HOT:
                return "order by likes desc, created_date desc";
            case NEW:
                return "order by created_date desc";
            case ANSWERS:
                return "order by answers desc";
            case SOLVED:
                return "where is_solved is true order by likes desc";
        }
        throw new BadRequestException("유효하지 않은 정렬 방식입니다.");
    }

    private RowMapper<Question> questionRowMapper() {
        return ((rs, rowNum) ->
                Question.builder()
                        .id(rs.getLong("id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .title(rs.getString("title"))
                        .text(rs.getString("text"))
                        .description(rs.getString("description"))
                        .likes(rs.getLong("likes"))
                        .tags(TagUtils.createStringListFromArray(rs.getArray("tags")))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .repliesCount(rs.getLong("replies_count"))
                        .answersCount(rs.getLong("answers"))
                        .isSolved(rs.getBoolean("is_solved"))
                        .memoId(rs.getLong("memo_id"))
                        .build());
    }

    private RowMapper<Question> questionLikeRowMapper() {
        return ((rs, rowNum) ->
                Question.builder()
                        .id(rs.getLong("id"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .title(rs.getString("title"))
                        .text(rs.getString("text"))
                        .description(rs.getString("description"))
                        .likes(rs.getLong("likes"))
                        .tags(TagUtils.createStringListFromArray(rs.getArray("tags")))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .repliesCount(rs.getLong("replies_count"))
                        .answersCount(rs.getLong("answers"))
                        .isSolved(rs.getBoolean("is_solved"))
                        .isLike(rs.getBoolean("is_like"))
                        .memoId(rs.getLong("memo_id"))
                        .build());
    }
}
