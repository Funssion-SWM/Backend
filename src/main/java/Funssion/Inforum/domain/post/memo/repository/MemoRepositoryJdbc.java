package Funssion.Inforum.domain.post.memo.repository;

import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.exception.etc.ArrayToListException;
import Funssion.Inforum.common.exception.etc.UpdateFailException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        String sql = "INSERT into post.memo (author_id, author_name, author_image_path, title, description, text, color, tags, is_temporary, is_created) " +
                "VALUES (?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?);";
        List<String> listOfTagsInMemo = memo.getMemoTags();
        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
            psmt.setLong(1, memo.getAuthorId());
            psmt.setString(2, memo.getAuthorName());
            psmt.setString(3, memo.getAuthorImagePath());
            psmt.setString(4, memo.getTitle());
            psmt.setString(5, memo.getDescription());
            psmt.setString(6, memo.getText());
            psmt.setString(7, memo.getColor());
            psmt.setArray(8,TagUtils.createSqlArray(template,listOfTagsInMemo));
            psmt.setBoolean(9, memo.getIsTemporary());
            psmt.setBoolean(10, !memo.getIsTemporary());
            return psmt;
        }, keyHolder);
        long createdMemoId = keyHolder.getKey().longValue();

        return findById(createdMemoId);
    }

    @Override
    public List<Memo> findAllByDaysOrderByLikes(DateType period, Long pageNum, Long memoCnt) {
        String sql = "select * from post.memo where created_date > current_date - CAST(? AS INTERVAL) and is_temporary = false " +
                "order by likes desc, id desc " +
                "limit ? offset ?";
        return template.query(sql, memoRowMapper(), period.getInterval(), memoCnt, pageNum * memoCnt);
    }

    @Override
    public List<Memo> findAllOrderById(Long pageNum, Long memoCnt) {
        String sql = "select * from post.memo where is_temporary = false order by id desc " +
                "limit ? offset ?";
        return template.query(sql, memoRowMapper(), memoCnt, pageNum * memoCnt);
    }

    @Override
    public List<Memo> findAllByUserIdOrderById(Long userId) {
        String sql = "select * from post.memo where author_id = ? and is_temporary = false order by id desc";
        return template.query(sql, memoRowMapper(), userId);
    }

    @Override
    public List<Memo> findAllLikedMemosByUserId(Long userId) {
        String sql = "select * from post.memo i join member.like l on i.id = l.post_id and l.post_type = 'MEMO' " +
                "where l.user_id = ? and is_temporary = false order by i.id desc";
        return template.query(sql, memoRowMapper(), userId);
    }

    @Override
    public List<Memo> findAllDraftMemosByUserId(Long userId) {
        String sql = "select * from post.memo where author_id = ? and is_temporary = true order by id desc";

        return template.query(sql, memoRowMapper(), userId);
    }


    @Override
    public List<Memo> findAllBySearchQuery(List<String> searchStringList, OrderType orderType, Long userId) {
        String sql = getSql(searchStringList, orderType, userId);

        return template.query(sql, memoRowMapper(), getParams(userId, searchStringList));
    }
    private static String getSql(List<String> searchStringList, OrderType orderType, Long userId) {
        StringBuilder sql;

        if (!userId.equals(SecurityContextUtils.ANONYMOUS_USER_ID)) {
            sql = new StringBuilder("select * from (select * from post.memo where is_temporary = false and author_id = ?) m where ");
        } else {
            sql = new StringBuilder("select * from (select * from post.memo where is_temporary = false) m where ");
        }

        for (int i = 0; i < searchStringList.size() ; i++) {
            sql.append("title ilike ? or ");
        }

        for (int i = 0; i < searchStringList.size() ; i++) {
            sql.append("text::text ilike ? ");
            if (i != searchStringList.size() - 1) sql.append("or ");
        }

        sql.append(getOrderBySql(orderType));

        return sql.toString();
    }

    private static Object[] getParams(Long userId, List<String> searchStringList) {
        ArrayList<Object> params = new ArrayList<>();
        if (!userId.equals(SecurityContextUtils.ANONYMOUS_USER_ID)) {
            params.add(userId);
        }
        params.addAll(searchStringList);
        params.addAll(searchStringList);
        return params.toArray();
    }

    @Override
    public List<Memo> findAllByTag(String tagText, OrderType orderType) {
        String sql = "select * from post.memo where is_temporary = false and ? ilike any(tags)" + getOrderBySql(orderType);

        return template.query(sql, memoRowMapper(), tagText);
    }

    @Override
    public List<Memo> findAllByTag(String tagText, Long userId, OrderType orderType) {
        String sql = "select * from post.memo where author_id = ? and is_temporary = false and ? ilike any(tags)" + getOrderBySql(orderType);

        return template.query(sql, memoRowMapper(), userId, tagText);
    }

    private static String getOrderBySql(OrderType orderType) {
        switch (orderType) {
            case HOT -> {
                return  " order by likes desc, id desc";
            }
            case NEW -> {
                return  " order by id desc";
            }
        }
        throw new BadRequestException("Invalid orderType value");
    }

    @Override
    public List<Memo> findAllBySeriesId(Long seriesId) {
        String sql = "select * from post.memo where series_id = ? order by series_order";
        return template.query(sql, memoRowMapper(), seriesId);
    }

    @Override
    public Memo findById(Long id) {
        String sql = "select * from post.memo where id = ?";
        return template.query(sql, memoRowMapper(), id).stream().findAny().orElseThrow(() -> new MemoNotFoundException());
    }

    private RowMapper<Memo> memoRowMapper() {
        return ((rs, rowNum) ->
                Memo.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .description(rs.getString("description"))
                        .text(rs.getString("text"))
                        .color(rs.getString("color"))
                        .authorId(rs.getLong("author_id"))
                        .authorName(rs.getString("author_name"))
                        .authorImagePath(rs.getString("author_image_path"))
                        .repliesCount(rs.getLong("replies_count"))
                        .questionCount(rs.getLong("question_count"))
                        .memoTags(TagUtils.createStringListFromArray(rs.getArray("tags")))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime())
                        .likes(rs.getLong("likes"))
                        .isTemporary(rs.getBoolean("is_temporary"))
                        .isCreated(rs.getBoolean("is_created"))
                        .build());
    }

    @Override
    public Memo updateContentInMemo(MemoSaveDto form, Long memoId) {

        String sql = "update post.memo " +
                "set title = ?, description = ?, text = ?::jsonb, color = ?, tags = ?, updated_date = current_timestamp, is_temporary = ? " +
                "where id = ?";
        try {
            if (template.update(sql,
                    form.getMemoTitle(), form.getMemoDescription(), form.getMemoText(), form.getMemoColor(),TagUtils.createSqlArray(template,form.getMemoTags()), form.getIsTemporary(), memoId)
                    == 0)
                throw new MemoNotFoundException("update content fail");
        } catch (SQLException e) {
            throw new ArrayToListException("Javax.sql.Array (PostgreSQL의 array) 를 List로 변경할 때의 오류",e);
        }
        return findById(memoId);
    }

    @Override
    public Memo updateContentInMemo(MemoSaveDto form, Long memoId, Boolean isCreated) {

        String sql = "update post.memo " +
                "set title = ?, description = ?, text = ?::jsonb, color = ?, tags = ?, created_date = current_timestamp, updated_date = current_timestamp, is_temporary = ? , is_created = ? " +
                "where id = ?";
        try {
            if (template.update(sql,
                    form.getMemoTitle(), form.getMemoDescription(), form.getMemoText(), form.getMemoColor(),TagUtils.createSqlArray(template,form.getMemoTags()), form.getIsTemporary(), isCreated
                    , memoId)
                    == 0)
                throw new MemoNotFoundException("update content fail");
        } catch (SQLException e) {
            throw new ArrayToListException("Javax.sql.Array (PostgreSQL의 array) 를 List로 변경할 때의 오류",e);
        }
        return findById(memoId);
    }
    @Override
    public Memo updateLikesInMemo(Long likes, Long memoId) {
        String sql = "update post.memo " +
                "set likes = ? " +
                "where id = ?";

        if (template.update(sql, likes, memoId) == 0) throw new MemoNotFoundException("update likes fail");
        return findById(memoId);
    }

    @Override
    public void updateAuthorProfile(Long authorId, String authorProfileImagePath) {
        String sql = "update post.memo " +
                "set author_image_path = ? " +
                "where author_id = ?";

        template.update(sql, authorProfileImagePath, authorId);
    }

    @Override
    public void updateSeriesIds(Long seriesId, Long authorId, List<Long> memoIdList) {
        StringBuilder sql = new StringBuilder(
                "UPDATE post.memo " +
                "SET series_id = ?, series_order = nextval('post.memo_series_order_seq'::regclass) " +
                "WHERE is_temporary = false and author_Id = ? and id in (");
        ArrayList<Object> params = new ArrayList<>();
        params.add(seriesId);
        params.add(authorId);

        for (Long memoId : memoIdList.subList(0, memoIdList.size() - 1)) {
            sql.append("?,");
            params.add(memoId);
        }
        sql.append("?)");
        params.add(memoIdList.get(memoIdList.size() - 1));

        if (params.size() - 2 != template.update(sql.toString(), params.toArray()))
            throw new UpdateFailException("update fail: request size, updated rows are not same.");
    }

    @Override
    public void updateSeriesIdsToZero(Long seriesId, Long authorId) {
        String sql = "UPDATE post.memo " +
                    "SET series_id = 0 " +
                    "WHERE is_temporary = false AND author_Id = ? AND series_id = ?";

        template.update(sql, authorId, seriesId);
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from post.memo where id = ?";
        if (template.update(sql, id) == 0) throw new MemoNotFoundException("delete fail");
    }

    @Override
    public void updateQuestionsCountOfMemo(Long memoId, Sign sign) {
        String sql ="";
        switch(sign){
            case PLUS -> sql = "update post.memo set question_count = question_count + 1 where id = ?";
            case MINUS-> sql = "update post.memo set question_count = question_count - 1 where id = ?";
        }
        if(template.update(sql,memoId)==0) throw new MemoNotFoundException("update question count fail");
    }

    public Long getCommentsCount(Long id){
        String sql = "select replies_count from post.memo where id = ?";
        return template.queryForObject(sql,Long.class,id);
    }
}
