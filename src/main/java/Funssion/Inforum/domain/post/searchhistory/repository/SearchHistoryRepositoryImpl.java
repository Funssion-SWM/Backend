package Funssion.Inforum.domain.post.searchhistory.repository;

import Funssion.Inforum.domain.post.searchhistory.domain.SearchHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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
}
