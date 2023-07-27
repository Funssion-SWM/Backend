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
import java.util.Optional;

@Repository
@Slf4j
public class MemoRepositoryJdbc implements MemoRepository{

    private final JdbcTemplate template;


    public MemoRepositoryJdbc(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    // insert and return PK
    @Override
    public Integer create(Integer userId, String userName, MemoSaveDto form) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        log.debug("form = {}",form);

        String sql = "INSERT INTO memo.info (user_id, user_name, memo_title, memo_description, memo_text, memo_color, created_date, updated_date)\n" +
                "VALUES (?, ?, ?, ?, ?::jsonb, ?, ?, ?);";

        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psmt.setInt(1,userId);
            psmt.setString(2,userName);
            psmt.setString(3,form.getMemoTitle());
            psmt.setString(4,form.getMemoDescription());
            psmt.setString(5,form.getMemoText());
            psmt.setString(6,form.getMemoColor());
            psmt.setDate(7,Date.valueOf(LocalDate.now()));
            psmt.setDate(8,null);
            return psmt;
        }, keyHolder);

        return (Integer) keyHolder.getKeys().get("memo_id");
    }

    @Override
    public List<MemoListDto> findAllByPeriodWithMostPopular(Integer period) {
        String sql = "select * from memo.info where created_date >= current_date - CAST(? AS int) order by likes desc";
        return template.query(sql,MemoListDto.memoListRowMapper(), period);
    }

    @Override
    public List<MemoListDto> findAllWithNewest() {
        String sql = "select * from memo.info order by memo_id desc";
        return template.query(sql, MemoListDto.memoListRowMapper());
    }

    @Override
    public Optional<MemoDto> findById(Integer id) {
        String sql = "select * from memo.info where memo_id = ?";
        return template.query(sql, MemoDto.memoRowMapper(), id).stream().findAny();
    }

    public String findByUserId(Integer userId) {
        String sql = "select user_name from member.member_user where user_id = ?";
        return template.queryForObject(sql, String.class, userId);
    }

    @Override
    public Integer update(Integer memoId, Integer userId, MemoSaveDto form) {
        String sql = "update memo.info set memo_title = ?, memo_text = ?::jsonb, memo_color = ?, updated_date = ? where memo_id = ? and user_id = ?";
        return template.update(sql, form.getMemoTitle(), form.getMemoText(), form.getMemoColor(), Date.valueOf(LocalDate.now()), memoId, userId);
    }

    @Override
    public Integer delete(Integer id) {
        String sql = "delete from memo.info where memo_id = ?";
        return template.update(sql, id);
    }
}
