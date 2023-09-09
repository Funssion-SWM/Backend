package Funssion.Inforum.domain.post.searchhistory.repository;


import Funssion.Inforum.domain.post.searchhistory.domain.SearchHistory;

import java.util.List;

public interface SearchHistoryRepository {

    void save(SearchHistory history);
    List<SearchHistory> findAllByUserIdRecent10(Long userId);
    void delete(Long id);
}
