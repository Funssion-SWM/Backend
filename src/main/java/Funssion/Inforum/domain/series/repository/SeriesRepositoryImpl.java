package Funssion.Inforum.domain.series.repository;

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
