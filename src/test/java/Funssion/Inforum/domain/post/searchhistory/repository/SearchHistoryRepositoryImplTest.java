package Funssion.Inforum.domain.post.searchhistory.repository;

import Funssion.Inforum.domain.post.searchhistory.domain.SearchHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SearchHistoryRepositoryImplTest {

    @Autowired
    private SearchHistoryRepository repository;

    @Test
    @DisplayName("검색 기록 저장")
    void save() {
        repository.save(SearchHistory.builder()
                        .userId(1L)
                        .searchText("JPA")
                        .isTag(false)
                        .build());
    }
}