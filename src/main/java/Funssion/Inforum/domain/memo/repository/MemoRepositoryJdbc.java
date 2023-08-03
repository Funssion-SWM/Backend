package Funssion.Inforum.domain.memo.repository;

import Funssion.Inforum.domain.memo.entity.Memo;
import Funssion.Inforum.domain.memo.exception.MemoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@Slf4j
public class MemoRepositoryJdbc implements MemoRepository{

    private final JdbcTemplate template;


    public MemoRepositoryJdbc(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    // insert and return PK
    @Override
    public Memo create(Memo memo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO memo.info (author_id, author_name, memo_title, memo_description, memo_text, memo_color, created_date, updated_date)\n" +
                "VALUES (?, ?, ?, ?, ?::jsonb, ?, ?, ?);";

        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psmt.setInt(1, memo.getAuthorId());
            psmt.setString(2, memo.getAuthorName());
            psmt.setString(3, memo.getMemoTitle());
            psmt.setString(4, memo.getMemoDescription());
            psmt.setString(5, memo.getMemoText());
            psmt.setString(6, memo.getMemoColor());
            psmt.setDate(7, memo.getCreatedDate());
            psmt.setDate(8,memo.getUpdatedDate());
            return psmt;
        }, keyHolder);

        Integer memoId = (Integer) keyHolder.getKeys().get("memo_id");

        return findById(memoId);
    }

    @Override
    public List<Memo> findAllByDaysOrderByLikes(Integer days) {
        String sql = "select * from memo.info where created_date > current_date - CAST(? AS int) order by likes desc";
        return getMemos(new Object[]{days}, sql);
    }

    @Override
    public List<Memo> findAllOrderById() {
        String sql = "select * from memo.info order by memo_id desc";
        return getMemos(new Object[]{}, sql);
    }

    @Override
    public List<Memo> findAllByUserIdOrderById(Integer userId) {
        String sql = "select * from memo.info where author_id = ? order by memo_id desc";
        return getMemos(new Object[]{userId}, sql);
    }

    private List<Memo> getMemos(Object[] param, String sql) {
        List<Memo> memoList = template.query(sql, memoRowMapper(), param);
        if (memoList.isEmpty()) throw new MemoNotFoundException();
        return memoList;
    }

    @Override
    public Memo findById(Integer id) {
        String sql = "select * from memo.info where memo_id = ?";
        return template.query(sql, memoRowMapper(), id).stream().findAny().orElseThrow(() -> new MemoNotFoundException());
    }

    private RowMapper<Memo> memoRowMapper() {
        return ((rs, rowNum) ->
                Memo.builder()
                        .memoId(rs.getInt("memo_id"))
                        .memoTitle(rs.getString("memo_title"))
                        .memoDescription(rs.getString("memo_description"))
                        .memoText(rs.getString("memo_text"))
                        .memoColor(rs.getString("memo_color"))
                        .createdDate(rs.getDate("created_date"))
                        .authorId(rs.getInt("author_id"))
                        .authorName(rs.getString("author_name"))
                        .build());
    }

    @Override
    public Memo update(Memo memo, Integer memoId) {
        log.info("me {}", memo);
        String sql = "update memo.info set memo_title = ?, memo_description = ?, memo_text = ?::jsonb, memo_color = ?, updated_date = ? where memo_id = ? and author_id = ?";
        if (template.update(sql, memo.getMemoTitle(), memo.getMemoDescription(), memo.getMemoText(), memo.getMemoColor(), memo.getUpdatedDate(), memoId, memo.getAuthorId())
                == 0)
            throw new MemoNotFoundException("update fail");
        return findById(memoId);
    }

    @Override
    public void delete(Integer id) {
        String sql = "delete from memo.info where memo_id = ?";
        if (template.update(sql, id) == 0) throw new MemoNotFoundException("delete fail");
    }
}
