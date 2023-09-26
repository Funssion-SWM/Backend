package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.response.UploadedQuestionDto;
import Funssion.Inforum.domain.tag.TagUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class QuestionRepositoryImpl implements QuestionRepository {
    private final JdbcTemplate template;
    public QuestionRepositoryImpl(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public UploadedQuestionDto createQuestion(Question question) {
        List<String> questionTags = question.getTags();

        String sql = "insert into question.info(author_id, author_name, author_image_path, title, text, tags) " +
                "values(?,?,?,?,?::jsonb,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con->{
            PreparedStatement psmt = con.prepareStatement(sql,new String[]{"id"});
            psmt.setLong(1, question.getAuthorId());
            psmt.setString(2, question.getAuthorName());
            psmt.setString(3, question.getAuthorImagePath());
            psmt.setString(4, question.getTitle());
            psmt.setString(5, question.getText());
            psmt.setArray(6, TagUtils.createSqlArray(template,questionTags));
            return psmt;
        },keyHolder);

        return new UploadedQuestionDto(keyHolder.getKey().longValue(), "성공적으로 질문이 등록되었습니다.");
    }
}
