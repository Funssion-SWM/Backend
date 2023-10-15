package Funssion.Inforum.domain.series.repository;

import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.exception.etc.DeleteFailException;
import Funssion.Inforum.common.exception.etc.UpdateFailException;
import Funssion.Inforum.domain.series.domain.Series;
import Funssion.Inforum.domain.series.dto.request.SeriesRequestDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class SeriesRepositoryImpl implements SeriesRepository {

    private final JdbcTemplate template;

    public SeriesRepositoryImpl (DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }
    @Override
    public Long create(Series series) {
        String sql = "INSERT into post.series (author_id, author_name, author_image_path, title, description, thumbnail_image_path) " +
                "VALUES (?, ?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
            psmt.setLong(1, series.getAuthorId());
            psmt.setString(2, series.getAuthorName());
            psmt.setString(3, series.getAuthorImagePath());
            psmt.setString(4, series.getTitle());
            psmt.setString(5, series.getDescription());
            psmt.setString(6, series.getThumbnailImagePath());
            return psmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public Optional<Series> findById(Long id) {
        String sql = "SELECT * " +
                "FROM post.series " +
                "WHERE id = ?";

        return template.query(sql, seriesRowMapper(), id).stream().findAny();
    }

    @Override
    public List<Series> findAllBy(DateType period, OrderType orderBy, Long pageNum, Long resultCntPerPage) {
        String sql = "SELECT * " +
                "FROM post.series " +
                "WHERE created > current_timestamp - interval ? " +
                "ORDER BY " + orderBySql(orderBy) +
                "LIMIT ? OFFSET ?";

        return template.query(sql, seriesRowMapper(), period.getInterval(), pageNum, pageNum * resultCntPerPage);
    }

    @Override
    public List<Series> findAllBy(Long authorId, DateType period, OrderType orderBy, Long pageNum, Long resultCntPerPage) {
        String sql = "SELECT * " +
                "FROM post.series " +
                "WHERE author_id = ? and created > current_timestamp - interval ? " +
                "ORDER BY " + orderBySql(orderBy) +
                "LIMIT ? OFFSET ?";

        return template.query(sql, seriesRowMapper(), authorId, period.getInterval(), pageNum, pageNum * resultCntPerPage);
    }

    @Override
    public List<Series> findAllBy(List<String> searhStringList, DateType period, OrderType orderBy, Long pageNum, Long resultCntPerPage) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(period.getInterval());
        String sql = "SELECT * " +
                "FROM post.series " +
                "WHERE created > current_timestamp - interval ? and (" + searchConditionalStatement(searhStringList, params) + ") " +
                "ORDER BY " + orderBySql(orderBy) +
                "LIMIT ? OFFSET ?";

        params.add(pageNum);
        params.add(resultCntPerPage);

        return template.query(sql, seriesRowMapper(), params);
    }

    private String searchConditionalStatement(List<String> searhStringList, ArrayList<Object> params) {
        StringBuilder conditionalStatement = new StringBuilder();
        String lastSeachString = searhStringList.get(searhStringList.size() - 1);
        for (String searchString : searhStringList) {
            if (searchString.equals(lastSeachString)) {
                conditionalStatement.append("title = ? or description = ?");
            } else {
                conditionalStatement.append("title = ? or description = ? or ");
            }
            params.add(searchString);
            params.add(searchString);
        }
        return conditionalStatement.toString();
    }

    private String orderBySql(OrderType orderBy) {
        switch (orderBy) {
            case HOT -> {
                return "likes desc, created desc ";
            }
            case NEW -> {
                return "created desc ";
            }
            default -> throw new BadRequestException("not matched order type");
        }
    }

    private RowMapper<Series> seriesRowMapper() {
        return (rs, rowNum) -> Series.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .thumbnailImagePath(rs.getString("thumbnail_image_path"))
                .authorId(rs.getLong("author_id"))
                .authorName(rs.getString("author_name"))
                .authorImagePath(rs.getString("author_image_path"))
                .likes(rs.getLong("likes"))
                .createdDate(rs.getTimestamp("created").toLocalDateTime())
                .build();
    }

    @Override
    public void update(Long seriesId, SeriesRequestDto seriesRequestDto, String newThumbnailImagePath) {
        String sql = "UPDATE post.series " +
                        "SET title = ?, description = ?, thumbnail_image_path = ? " +
                        "where id = ?";

        if (template.update(
                sql,
                seriesRequestDto.getTitle(), seriesRequestDto.getDescription(), newThumbnailImagePath,
                seriesId
        ) != 1) {
            throw new UpdateFailException("series update fail id = " + seriesId);
        };
    }

    @Override
    public void delete(Long seriesId) {
        String sql = "DELETE FROM post.series " +
                "WHERE id = ?";

        if (template.update(sql, seriesId) != 1) {
            throw new DeleteFailException("delete series fail id = " + seriesId);
        }
    }
}
