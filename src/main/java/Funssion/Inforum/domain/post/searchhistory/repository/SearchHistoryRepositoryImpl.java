package Funssion.Inforum.domain.post.searchhistory.repository;

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
    public List<SearchHistory> findSearchHistoryListByUserId(Long userId) {
        String sql = "select * from post.search_history where user_id = ?";

        return template.query(sql, searchHistoryRowMapper(), userId);
    }

    private RowMapper<SearchHistory> searchHistoryRowMapper() {
        return (rs, rowNum) -> SearchHistory.builder()
                    .id(rs.getLong("id"))
                    .searchText(rs.getString("search_text"))
                    .isTag(rs.getBoolean("is_tag"))
                    .build();

    }
}
