package Funssion.Inforum.memo.repository;

import Funssion.Inforum.memo.entity.Memo;
import Funssion.Inforum.memo.form.MemoSaveForm;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@Repository
public class MemoRepositoryH2 implements MemoRepository{

    private final JdbcTemplate template;

    public MemoRepositoryH2(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Memo create(int userId, String userName, MemoSaveForm form) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO memo (user_id, user_name, memo_title, memo_text, memo_color, created_date, updated_date)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";
        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psmt.setInt(1,userId);
            psmt.setString(2,userName);
            psmt.setString(3,form.getMemoTitle());
            psmt.setObject(4,form.getMemoText());
            psmt.setString(5,form.getMemoColor());
            psmt.setDate(6,Date.valueOf(LocalDate.now()));
            psmt.setDate(7,null);
            return psmt;
        }, keyHolder);

        return findById(keyHolder.getKey().intValue());
    }

    @Override
    public List<Memo> findAllByUserId(int userId) {
        String sql = "select * from memo where user_id = ?";
        return template.query(sql, memoRowMapper(), userId);
    }

    @Override
    public List<Memo> findAllByPeriod(int period) {
        String sql = "select * from memo where created_date >= current_date - CAST(? AS int) order by created_date desc";
        return template.query(sql,memoRowMapper(), period);
    }

    @Override
    public Memo findById(int id) {
        String sql = "select * from memo where memo_id = ? order by created_date desc";
        return template.queryForObject(sql, memoRowMapper(), id);
    }

    @Override
    public Memo update(int id, MemoSaveForm form) {
        String sql = "update memo set memo_title = ?, memo_text = ?, memo_color = ? where memo_id = ?";
        template.update(sql,form.getMemoTitle(), form.getMemoText(), form.getMemoColor(), id);
        return findById(id);
    }

    @Override
    public void delete(int id) {
        String sql = "delete from memo where memo_id = ?";
        template.update(sql, id);
    }

    private RowMapper<Memo> memoRowMapper() {
        return ((rs, rowNum) -> {
            return new Memo(rs.getInt("memo_id"), rs.getInt("user_id"), rs.getString("user_name"),
                    rs.getString("memo_title"), rs.getString("memo_text"), rs.getString("memo_color"),
                    rs.getDate("created_date"), rs.getDate("updated_date"));
        });
    }
}
