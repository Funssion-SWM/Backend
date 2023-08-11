package Funssion.Inforum.domain.post.memo.repository;

import Funssion.Inforum.domain.post.domain.Post;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.exception.MemoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
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

        String sql = "INSERT into memo.info (author_id, memo_title, memo_description, memo_text, memo_color, created_date, updated_date)\n" +
                "VALUES (?, ?, ?, ?::jsonb, ?, ?, ?);";

        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"memo_id"});
            psmt.setLong(1, memo.getAuthorId());
            psmt.setString(2, memo.getTitle());
            psmt.setString(3, memo.getDescription());
            psmt.setString(4, memo.getText());
            psmt.setString(5, memo.getColor());
            psmt.setDate(6, memo.getCreatedDate());
            psmt.setDate(7,memo.getUpdatedDate());
            return psmt;
        }, keyHolder);

        return findById(keyHolder.getKey().longValue());
    }

    @Override
    public List<Memo> findAllByDaysOrderByLikes(Long days) {
        String sql = "select * from memo.info where created_date > current_date - CAST(? AS int) order by likes, memo_id desc";
        return template.query(sql, memoRowMapper(), days);
    }

    @Override
    public List<Memo> findAllOrderById() {
        String sql = "select * from memo.info order by memo_id desc";
        return template.query(sql, memoRowMapper());
    }

    @Override
    public List<Memo> findAllByUserIdOrderById(Long userId) {
        String sql = "select * from memo.info where author_id = ? order by memo_id desc";
        return template.query(sql, memoRowMapper(), userId);
    }

    @Override
    public Memo findById(Long id) {
        String sql = "select * from memo.info where memo_id = ?";
        return template.query(sql, memoRowMapper(), id).stream().findAny().orElseThrow(() -> new MemoNotFoundException());
    }

    private RowMapper<Memo> memoRowMapper() {
        return ((rs, rowNum) ->
                Memo.builder()
                        .id(rs.getLong("memo_id"))
                        .title(rs.getString("memo_title"))
                        .description(rs.getString("memo_description"))
                        .text(rs.getString("memo_text"))
                        .color(rs.getString("memo_color"))
                        .createdDate(rs.getDate("created_date"))
                        .authorId(rs.getLong("author_id"))
                        .likes(rs.getLong("likes"))
                        .build());
    }

    @Override
    public Memo update(Memo memo, Long memoId) {

        String sql = "update memo.info " +
                "set memo_title = ?, memo_description = ?, memo_text = ?::jsonb, memo_color = ?, updated_date = ?, likes = ?" +
                "where memo_id = ? and author_id = ?";

        if (template.update(sql,
                memo.getTitle(), memo.getDescription(), memo.getText(), memo.getColor(), memo.getUpdatedDate(), memo.getLikes(),
                memoId, memo.getAuthorId())
                == 0)
            throw new MemoNotFoundException("update fail");
        return findById(memoId);
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from memo.info where memo_id = ?";
        if (template.update(sql, id) == 0) throw new MemoNotFoundException("delete fail");
    }
}
