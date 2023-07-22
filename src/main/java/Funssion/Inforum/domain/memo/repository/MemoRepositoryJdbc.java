package Funssion.Inforum.domain.memo.repository;

import Funssion.Inforum.domain.memo.dto.MemoDto;
import Funssion.Inforum.domain.memo.dto.MemoListDto;
import Funssion.Inforum.domain.memo.dto.MemoSaveDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
@Slf4j
public class MemoRepositoryJdbc implements MemoRepository{

    private final JdbcTemplate template;

    public MemoRepositoryJdbc(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public MemoDto create(int userId, String userName, MemoSaveDto form) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        log.debug("form = {}",form);

        String sql = "INSERT INTO memo.info (user_id, user_name, memo_title, memo_text, memo_color, created_date, updated_date)\n" +
                "VALUES (?, ?, ?, ?::jsonb, ?, ?, ?);";
        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psmt.setInt(1,userId);
            psmt.setString(2,userName);
            psmt.setString(3,form.getMemoTitle());
            psmt.setString(4,form.getMemoText().toString());
            psmt.setString(5,form.getMemoColor());
            psmt.setDate(6,Date.valueOf(LocalDate.now()));
            psmt.setDate(7,null);
            return psmt;
        }, keyHolder);

        return findById((Integer) keyHolder.getKeys().get("memo_id"));
    }

    @Override
    public List<MemoListDto> findAllByPeriodWithMostPopular(int period) {
        String sql = "select * from memo.info where created_date >= current_date - CAST(? AS int) order by likes desc";
        return template.query(sql,MemoListDto.memoListRowMapper(), period);
    }

    @Override
    public List<MemoListDto> findAllWithNewest() {
        String sql = "select * from memo.info order by created_date desc";
        return template.query(sql, MemoListDto.memoListRowMapper());
    }

    @Override
    public MemoDto findById(int id) {
        String sql = "select * from memo.info where memo_id = ? order by created_date desc";
        List<MemoDto> memos = template.query(sql, MemoDto.memoRowMapper(), id);
        if (memos.size() != 1) throw new NoSuchElementException("memo not found");
        return memos.get(0);
    }

    public String findByUserId(Integer userId) {
        String sql = "select user_name from member.member_user where user_id = ?";
        return template.queryForObject(sql, String.class, userId);
    }

    @Override
    public MemoDto update(int id, MemoSaveDto form) {
        String sql = "update memo.info set memo_title = ?, memo_text = ?, memo_color = ?, updated_date = ? where memo_id = ?";
        if (template.update(sql, form.getMemoTitle(), form.getMemoText(), form.getMemoColor(), Date.valueOf(LocalDate.now()), id) == 0)
            throw new NoSuchElementException("memo not found");
        return findById(id);
    }

    @Override
    public void delete(int id) {
        String sql = "delete from memo.info where memo_id = ?";
        if (template.update(sql, id) == 0)
            throw new NoSuchElementException("memo not found");

    }
}
