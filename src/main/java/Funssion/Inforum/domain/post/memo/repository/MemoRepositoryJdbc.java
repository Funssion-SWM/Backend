package Funssion.Inforum.domain.post.memo.repository;

import Funssion.Inforum.common.constant.memo.MemoOrderType;
import Funssion.Inforum.domain.post.domain.Post;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.exception.MemoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
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

        String sql = "INSERT into memo.info (author_id, author_name, author_image_path, memo_title, memo_description, memo_text, memo_color, created_date, updated_date)\n" +
                "VALUES (?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?);";

        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"memo_id"});
            psmt.setLong(1, memo.getAuthorId());
            psmt.setString(2, memo.getAuthorName());
            psmt.setString(3, memo.getAuthorImagePath());
            psmt.setString(4, memo.getTitle());
            psmt.setString(5, memo.getDescription());
            psmt.setString(6, memo.getText());
            psmt.setString(7, memo.getColor());
            psmt.setDate(8, memo.getCreatedDate());
            psmt.setDate(9,memo.getUpdatedDate());
            return psmt;
        }, keyHolder);

        return findById(keyHolder.getKey().longValue());
    }

    @Override
    public List<Memo> findAllByDaysOrderByLikes(Long days) {
        String sql = "select * from memo.info where created_date > current_date - CAST(? AS int) order by likes desc, memo_id desc";
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
    public List<Memo> findAllLikedMemosByUserId(Long userId) {
        String sql = "select * from memo.info i join member.like l on i.memo_id = l.post_id and l.post_type = 'MEMO' " +
                "where l.user_id = ?";

        return template.query(sql, memoRowMapper(), userId);
    }


    @Override
    public List<Memo> findAllBySearchQuery(List<String> searchStringList, MemoOrderType orderType) {
        String sql = getSql(searchStringList, orderType);

        return template.query(sql, memoRowMapper(), getParams(searchStringList));
    }

    private static String getSql(List<String> searchStringList, MemoOrderType orderType) {
        String sql = "select * from memo.info where ";

        for (int i = 0; i < searchStringList.size() ; i++) {
            sql += "memo_title like ? or ";
        }

        for (int i = 0; i < searchStringList.size() ; i++) {
            sql += "memo_text::text like ? ";
            if (i != searchStringList.size() - 1) sql += "or ";
        }

        switch (orderType) {
            case HOT -> sql += "order by likes desc, memo_id desc";
            case NEW -> sql += "order by memo_id desc";
        }

        return sql;
    }

    private static Object[] getParams(List<String> searchStringList) {
        ArrayList<String> params = new ArrayList<>();
        params.addAll(searchStringList);
        params.addAll(searchStringList);
        return params.stream().toArray();
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
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .createdDate(rs.getDate("created_date"))
                        .updatedDate(rs.getDate("updated_date"))
                        .likes(rs.getLong("likes"))
                        .build());
    }

    @Override
    public Memo updateContentInMemo(MemoSaveDto form, Long memoId) {

        String sql = "update memo.info " +
                "set memo_title = ?, memo_description = ?, memo_text = ?::jsonb, memo_color = ?, updated_date = ?" +
                "where memo_id = ?";

        if (template.update(sql,
                form.getMemoTitle(), form.getMemoDescription(), form.getMemoText(), form.getMemoColor(), Date.valueOf(LocalDate.now()),
                memoId)
                == 0)
            throw new MemoNotFoundException("update content fail");
        return findById(memoId);
    }

    @Override
    public Memo updateLikesInMemo(Long likes, Long memoId) {
        String sql = "update memo.info " +
                "set likes = ? " +
                "where memo_id = ?";

        if (template.update(sql, likes, memoId) == 0) throw new MemoNotFoundException("update likes fail");
        return findById(memoId);
    }

    @Override
    public void updateAuthorProfile(Long authorId, String authorProfileImagePath) {
        String sql = "update memo.info " +
                "set author_image_path = ? " +
                "where author_id = ?";

        log.info("{}", template.update(sql, authorProfileImagePath, authorId));
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from memo.info where memo_id = ?";
        if (template.update(sql, id) == 0) throw new MemoNotFoundException("delete fail");
    }
}
