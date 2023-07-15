package Funssion.Inforum.memo.repository;

import Funssion.Inforum.memo.dto.MemoDto;
import Funssion.Inforum.memo.dto.MemoListDto;
import Funssion.Inforum.memo.entity.Memo;
import Funssion.Inforum.memo.dto.MemoSaveDto;
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
import java.util.NoSuchElementException;

@Repository
public class MemoRepositoryH2 implements MemoRepository{

    private final JdbcTemplate template;

    public MemoRepositoryH2(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public MemoDto create(int userId, String userName, MemoSaveDto form) {
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
    public List<MemoListDto> findAllByUserId(int userId) {
        String sql = "select * from memo where user_id = ?";
        return template.query(sql, memoListRowMapper(), userId);
    }

    @Override
    public List<MemoListDto> findAllByPeriodWithMostPopular(int period) {
        String sql = "select * from memo where created_date >= current_date - CAST(? AS int) order by likes desc";
        return template.query(sql,memoListRowMapper(), period);
    }

    @Override
    public List<MemoListDto> findAllWithNewest() {
        String sql = "select * from memo order by created_date desc";
        return template.query(sql, memoListRowMapper());
    }

    @Override
    public MemoDto findById(int id) {
        String sql = "select * from memo where memo_id = ? order by created_date desc";
        List<MemoDto> memos = template.query(sql, memoRowMapper(), id);
        if (memos.size() != 1) throw new NoSuchElementException("memo not found");
        return memos.get(0);
    }

    @Override
    public MemoDto update(int id, MemoSaveDto form) {
        String sql = "update memo set memo_title = ?, memo_text = ?, memo_color = ?, updated_date = ? where memo_id = ?";
        if (template.update(sql, form.getMemoTitle(), form.getMemoText(), form.getMemoColor(), Date.valueOf(LocalDate.now()), id) == 0)
            throw new NoSuchElementException("memo not found");
        return findById(id);
    }

    @Override
    public void delete(int id) {
        String sql = "delete from memo where memo_id = ?";
        if (template.update(sql, id) == 0)
            throw new NoSuchElementException("memo not found");
    }

    private RowMapper<MemoListDto> memoListRowMapper() {
        return ((rs, rowNum) ->
                MemoListDto.builder()
                        .memoId(rs.getInt("memo_id"))
                        .memoTitle(rs.getString("memo_title"))
                        .memoText(rs.getString("memo_text"))
                        .memoColor(rs.getString("memo_color"))
                        .createdDate(rs.getDate("created_date"))
                        .authorId(rs.getInt("user_id"))
                        .authorName(rs.getString("user_name"))
                        .build());
    }


    private RowMapper<MemoDto> memoRowMapper() {
        return ((rs, rowNum) ->
                MemoDto.builder()
                    .memoId(rs.getInt("memo_id"))
                    .userId(rs.getInt("user_id"))
                    .userName(rs.getString("user_name"))
                    .memoTitle(rs.getString("memo_title"))
                    .memoText(rs.getString("memo_text"))
                    .memoColor(rs.getString("memo_color"))
                    .createdDate(rs.getDate("created_date"))
                    .updatedDate(rs.getDate("updated_date"))
                    .build()
        );
    }
}
