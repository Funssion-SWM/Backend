package Funssion.Inforum.domain.post.memo.repository;

import Funssion.Inforum.common.constant.memo.MemoOrderType;
import Funssion.Inforum.common.exception.ArrayToListException;
import Funssion.Inforum.common.exception.BadRequestException;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import Funssion.Inforum.domain.post.memo.exception.MemoNotFoundException;
import Funssion.Inforum.domain.tag.TagUtils;
import Funssion.Inforum.domain.tag.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class MemoRepositoryJdbc implements MemoRepository{

    private final JdbcTemplate template;

    public MemoRepositoryJdbc(DataSource dataSource, TagRepository tagRepository) {
        this.template = new JdbcTemplate(dataSource);
    }

    // insert and return PK
    @Override
    public Memo create(Memo memo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT into memo.info (author_id, author_name, author_image_path, memo_title, memo_description, memo_text, memo_color, tags, is_temporary) " +
                "VALUES (?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?);";
        List<String> listOfTagsInMemo = memo.getMemoTags();
        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"memo_id"});
            psmt.setLong(1, memo.getAuthorId());
            psmt.setString(2, memo.getAuthorName());
            psmt.setString(3, memo.getAuthorImagePath());
            psmt.setString(4, memo.getTitle());
            psmt.setString(5, memo.getDescription());
            psmt.setString(6, memo.getText());
            psmt.setString(7, memo.getColor());
            psmt.setArray(8,TagUtils.createSqlArray(template,listOfTagsInMemo));
            psmt.setBoolean(9, memo.getIsTemporary());
            return psmt;
        }, keyHolder);
        long createdMemoId = keyHolder.getKey().longValue();

        return findById(createdMemoId);
    }

    @Override
    public List<Memo> findAllByDaysOrderByLikes(Long days) {
        String sql = "select * from memo.info where created_date > current_date - CAST(? AS int) and is_temporary = false " +
                "order by likes desc, memo_id desc";
        return template.query(sql, memoRowMapper(), days);
    }

    @Override
    public List<Memo> findAllOrderById() {
        String sql = "select * from memo.info where is_temporary = false order by memo_id desc";
        return template.query(sql, memoRowMapper());
    }

    @Override
    public List<Memo> findAllByUserIdOrderById(Long userId) {
        String sql = "select * from memo.info where author_id = ? and is_temporary = false order by memo_id desc";
        return template.query(sql, memoRowMapper(), userId);
    }

    @Override
    public List<Memo> findAllLikedMemosByUserId(Long userId) {
        String sql = "select * from memo.info i join member.like l on i.memo_id = l.post_id and l.post_type = 'MEMO' " +
                "where l.user_id = ? and is_temporary = false order by memo_id desc";

        return template.query(sql, memoRowMapper(), userId);
    }

    @Override
    public List<Memo> findAllDraftMemosByUserId(Long userId) {
        String sql = "select * from memo.info where author_id = ? and is_temporary = true order by memo_id desc";

        return template.query(sql, memoRowMapper(), userId);
    }


    @Override
    public List<Memo> findAllBySearchQuery(List<String> searchStringList, MemoOrderType orderType) {
        String sql = getSql(searchStringList, orderType);

        return template.query(sql, memoRowMapper(), getParams(searchStringList));
    }

    private static String getSql(List<String> searchStringList, MemoOrderType orderType) {
        String sql = "select * from memo.info where is_temporary = false and ";

        for (int i = 0; i < searchStringList.size() ; i++) {
            sql += "memo_title like ? or ";
        }

        for (int i = 0; i < searchStringList.size() ; i++) {
            sql += "memo_text::text like ? ";
            if (i != searchStringList.size() - 1) sql += "or ";
        }

        sql += getOrderBySql(orderType);

        return sql;
    }

    private static Object[] getParams(List<String> searchStringList) {
        ArrayList<String> params = new ArrayList<>();
        params.addAll(searchStringList);
        params.addAll(searchStringList);
        return params.stream().toArray();
    }

    @Override
    public List<Memo> findAllByTag(String tagText, MemoOrderType orderType) {
        String sql = "select * from memo.info where is_temporary = false and ? ilike any(tags)" + getOrderBySql(orderType);

        return template.query(sql, memoRowMapper(), tagText);
    }

    private static String getOrderBySql(MemoOrderType orderType) {
        switch (orderType) {
            case HOT -> {
                return  " order by likes desc, memo_id desc";
            }
            case NEW -> {
                return  " order by memo_id desc";
            }
        }
        throw new BadRequestException("Invalid orderType value");
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
                        .memoTags(TagUtils.createStringListFromArray(rs.getArray("tags")))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .likes(rs.getLong("likes"))
                        .isTemporary(rs.getBoolean("is_temporary"))
                        .build());
    }

    @Override
    public Memo updateContentInMemo(MemoSaveDto form, Long memoId) {

        String sql = "update memo.info " +
                "set memo_title = ?, memo_description = ?, memo_text = ?::jsonb, memo_color = ?, tags = ?, updated_date = ?, is_temporary = ? " +
                "where memo_id = ?";
        try {
            if (template.update(sql,
                    form.getMemoTitle(), form.getMemoDescription(), form.getMemoText(), form.getMemoColor(),TagUtils.createSqlArray(template,form.getMemoTags()), Date.valueOf(LocalDate.now()), form.getIsTemporary(), memoId)
                    == 0)
                throw new MemoNotFoundException("update content fail");
        } catch (SQLException e) {
            throw new ArrayToListException("Javax.sql.Array (PostgreSQL의 array) 를 List로 변경할 때의 오류",e);
        }
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

        template.update(sql, authorProfileImagePath, authorId);
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from memo.info where memo_id = ?";
        if (template.update(sql, id) == 0) throw new MemoNotFoundException("delete fail");
    }
}
