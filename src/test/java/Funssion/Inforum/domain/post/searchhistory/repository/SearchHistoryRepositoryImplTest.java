package Funssion.Inforum.domain.post.searchhistory.repository;

import Funssion.Inforum.domain.post.searchhistory.domain.SearchHistory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SearchHistoryRepositoryImplTest {

    private static final Long TEST_USER_ID = 987654321L;

    @Autowired
    private SearchHistoryRepository repository;

    private static SearchHistory history1;
    private static SearchHistory history2;

    @BeforeAll
    static void init() {
        history1 = SearchHistory.builder()
                .userId(TEST_USER_ID)
                .searchText("JPA")
                .isTag(false)
                .build();
        history2 = SearchHistory.builder()
                .userId(TEST_USER_ID)
                .searchText("JDK")
                .isTag(true)
                .build();
    }

    @Test
    @DisplayName("검색 기록 저장")
    void save() {
        repository.save(history1);
    }

    @Test
    @DisplayName("최근 검색 기록 10개 조회")
    void findAllByUserIdRecent10() {
        repository.save(history1);
        repository.save(history2);

        List<SearchHistory> foundList = repository.findAllByUserIdRecent10(TEST_USER_ID);

        Assertions.assertThat(foundList.size()).isEqualTo(2);
        Assertions.assertThat(foundList.get(0).getSearchText()).isEqualTo(history2.getSearchText());
        Assertions.assertThat(foundList.get(1).getSearchText()).isEqualTo(history1.getSearchText());
    }
}