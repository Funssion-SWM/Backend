package Funssion.Inforum.domain.post.searchhistory.repository;

import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.post.searchhistory.domain.SearchHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SearchHistoryRepositoryImpl implements SearchHistoryRepository {

    private final JdbcTemplate template;

    public SearchHistoryRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public void save(SearchHistory history) {
        String sql = "INSERT into post.search_history (user_id, search_text, is_tag) " +
                "VALUES (?, ?, ?);";

        template.update(sql, history.getUserId(), history.getSearchText(), history.getIsTag());
    }

    @Override
    public List<SearchHistory> findAllByUserIdRecent10(Long userId) {
        String sql = "select * from post.search_history where user_id = ? order by id desc limit 10";

        return template.query(sql, searchHistoryRowMapper(), userId);
    }

    private RowMapper<SearchHistory> searchHistoryRowMapper() {
        return (rs, rowNum) -> SearchHistory.builder()
                    .id(rs.getLong("id"))
                    .searchText(rs.getString("search_text"))
                    .isTag(rs.getBoolean("is_tag"))
                    .build();

    }

    @Override
    public void delete(Long id) {
        String sql = "delete from post.search_history where id = ?";

        if (template.update(sql, id) == 0)
            throw new NotFoundException("search history not found id = " + id);
    }
}
