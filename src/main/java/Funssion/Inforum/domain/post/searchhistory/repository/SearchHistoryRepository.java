package Funssion.Inforum.domain.post.searchhistory.repository;


import Funssion.Inforum.domain.post.searchhistory.domain.SearchHistory;

public interface SearchHistoryRepository {

    void save(SearchHistory history);
}
