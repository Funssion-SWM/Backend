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
import org.springframework.dao.DataIntegrityViolationException;
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

import static Funssion.Inforum.common.utils.CustomStringUtils.parseNullableStringtoLong;

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

        String sql = "INSERT into post.memo (author_id, author_name, author_image_path, title, description, text, color, tags, is_temporary, is_created, author_rank) " +
                "VALUES (?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?, ?);";
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
            psmt.setString(11,memo.getRank());
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
    public List<Memo> findAllByUserIdOrderById(Long userId, Long pageNum, Long resultCntPerPage) {
        String sql = "select * from post.memo where author_id = ? and is_temporary = false order by id desc limit ? offset ?";
        return template.query(sql, memoRowMapper(), userId, resultCntPerPage, resultCntPerPage*pageNum);
    }

    @Override
    public List<Memo> findAllLikedMemosByUserId(Long userId, Long pageNum, Long resultCntPerPage) {
        String sql = "select * from post.memo i join member.like l on i.id = l.post_id and l.post_type = 'MEMO' " +
                "where l.user_id = ? and is_temporary = false order by i.id desc " +
                "limit ? offset ?";
        return template.query(sql, memoRowMapper(), userId, resultCntPerPage, resultCntPerPage*pageNum);
    }

    @Override
    public List<Memo> findAllByTagsOrderByMatchesAndLikes(Long memoId) {
        String sql = "SELECT m.* " +
                "FROM post.memo m , UNNEST(m.tags) tag_element " +
                "WHERE tag_element ILIKE ANY(ARRAY(SELECT tags FROM post.memo WHERE id = ? AND array_length(tags, 1) >= 1)) " +
                "AND NOT id = ? " +
                "AND is_temporary = false " +
                "GROUP BY id " +
                "ORDER BY COUNT(tag_element) desc, likes desc, id desc";

        return template.query(sql, memoRowMapper(), memoId, memoId);
    }

    @Override
    public List<Memo> findAllDraftMemosByUserId(Long userId) {
        String sql = "select * from post.memo where author_id = ? and is_temporary = true order by id desc";

        return template.query(sql, memoRowMapper(), userId);
    }


    @Override
    public List<Memo> findAllBySearchQuery(List<String> searchStringList, OrderType orderType, Long userId, Long pageNum, Long resultCntPerPage) {
        String sql = getSql(searchStringList, orderType, userId);

        return template.query(sql, memoRowMapper(), getParams(userId, searchStringList, pageNum, resultCntPerPage));
    }
    private static String getSql(List<String> searchStringList, OrderType orderType, Long userId) {
        StringBuilder sql;

        if (!userId.equals(SecurityContextUtils.ANONYMOUS_USER_ID)) {
            sql = new StringBuilder("select * from (select * from post.memo where is_temporary = false and author_id = ?) m where ");
        } else {
            sql = new StringBuilder("select * from (select * from post.memo where is_temporary = false) m where ");
        }

        for (int i = 0; i < searchStringList.size() ; i++) {
            sql.append("title ilike '%'||?||'%' or ");
        }

        for (int i = 0; i < searchStringList.size() ; i++) {
            sql.append("regexp_match(text::text, '\"text\": \"([^\"]*)'||?, 'i') IS NOT null ");
            if (i != searchStringList.size() - 1) sql.append("or ");
        }

        sql.append(getOrderBySql(orderType));

        sql.append("LIMIT ? OFFSET ?");

        return sql.toString();
    }

    private static Object[] getParams(Long userId, List<String> searchStringList, Long pageNum, Long resultCntPerPage) {
        ArrayList<Object> params = new ArrayList<>();
        if (!userId.equals(SecurityContextUtils.ANONYMOUS_USER_ID)) {
            params.add(userId);
        }
        params.addAll(searchStringList);
        params.addAll(searchStringList);
        params.add(resultCntPerPage);
        params.add(resultCntPerPage * pageNum);
        return params.toArray();
    }

    @Override
    public List<Memo> findAllByTag(String tagText, OrderType orderType, Long pageNum, Long resultCntPerPage) {
        String sql = "select * from post.memo where is_temporary = false and ? ilike any(tags)" +
                getOrderBySql(orderType) +
                "LIMIT ? OFFSET ?";

        return template.query(sql, memoRowMapper(), tagText, resultCntPerPage, resultCntPerPage * pageNum);
    }

    @Override
    public List<Memo> findAllByTag(String tagText, Long userId, OrderType orderType, Long pageNum, Long resultCntPerPage) {
        String sql = "select * from post.memo where author_id = ? and is_temporary = false and ? ilike any(tags)" +
                getOrderBySql(orderType) +
                "LIMIT ? OFFSET ?";

        return template.query(sql, memoRowMapper(), userId, tagText, resultCntPerPage, resultCntPerPage * pageNum);
    }

    private static String getOrderBySql(OrderType orderType) {
        switch (orderType) {
            case HOT -> {
                return  " order by likes desc, id desc ";
            }
            case NEW -> {
                return  " order by id desc ";
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
    public List<String> findTop3ColorsBySeriesId(Long seriesId) {
        String sql = "SELECT color FROM post.memo WHERE series_id = ? ORDER BY series_order LIMIT 3";
        return template.queryForList(sql, String.class, seriesId);
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
                        .seriesId(parseNullableStringtoLong(rs.getString("series_id")))
                        .seriesTitle(rs.getString("series_title"))
                        .isTemporary(rs.getBoolean("is_temporary"))
                        .isCreated(rs.getBoolean("is_created"))
                        .rank(rs.getString("author_rank"))
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
                    form.getMemoTitle(), form.getMemoDescription(), form.getMemoText(), form.getMemoColor(),TagUtils.createSqlArray(template,form.getMemoTags()), form.getIsTemporary(), isCreated, memoId)
                    == 0)
                throw new MemoNotFoundException("update content fail");
        } catch (SQLException e) {
            throw new ArrayToListException("Javax.sql.Array (PostgreSQL의 array) 를 List로 변경할 때의 오류",e);
        }
        return findById(memoId);
    }
    @Override
    public Memo updateLikesInMemo(Long memoId, Sign sign) {
        String sql = "update post.memo " +
                "set likes = likes + ? " +
                "where id = ?";

        try {
            int updatedRows = template.update(sql, sign.getValue(), memoId);
            if (updatedRows != 1) {
                throw new UpdateFailException("update likes in series fail id = " + memoId);
            }
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("좋아요 수는 0 아래로 내려갈 수 없습니다.", e);
        }
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
    public void updateSeriesIdAndTitle(Long seriesId, String seriesTitle, Long authorId, List<Long> memoIdList) {

        int updatedRows = 0;
        for (Long memoId : memoIdList) {
            String sql = "UPDATE post.memo " +
                    "SET series_id = ?, series_title = ?, series_order = nextval('post.memo_series_order_seq'::regclass) " +
                    "WHERE id = ? and author_id = ?";

            updatedRows += template.update(sql, seriesId, seriesTitle, memoId, authorId);
        }

        if (memoIdList.size() != updatedRows)
            throw new UpdateFailException("update fail: request size, updated rows are not same.");
    }

    @Override
    public void updateSeriesIdsToZero(Long seriesId, Long authorId) {
        String sql = "UPDATE post.memo " +
                    "SET series_id = NULL, series_title = NULL " +
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
