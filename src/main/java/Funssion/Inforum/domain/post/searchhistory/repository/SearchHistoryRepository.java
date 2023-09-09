package Funssion.Inforum.domain.post.searchhistory.repository;


import Funssion.Inforum.domain.post.searchhistory.domain.SearchHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchHistoryRepository {

    void save(SearchHistory history);
    List<SearchHistory> findAllByUserIdRecent10(Long userId);
    void delete(Long id);
    void updateTime(Long id, LocalDateTime time);
}
