package Funssion.Inforum.domain.post.searchhistory.repository;

import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.post.searchhistory.domain.SearchHistory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Slf4j
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
        log.info("{}",foundList);

        assertThat(foundList.size()).isEqualTo(2);
        assertThat(foundList.get(0).getSearchText()).isEqualTo(history2.getSearchText());
        assertThat(foundList.get(1).getSearchText()).isEqualTo(history1.getSearchText());
    }

    @Test
    @DisplayName("검색 기록 삭제")
    void delete() {
        repository.save(history1);
        SearchHistory saved = repository.findAllByUserIdRecent10(history1.getUserId()).get(0);

        repository.delete(saved.getId());
        List<SearchHistory> savedList = repository.findAllByUserIdRecent10(history1.getUserId());

        assertThat(savedList.size()).isEqualTo(0);
        assertThatThrownBy(() -> repository.delete(saved.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("검색 기록 시간 수정")
    void updateTime() {
        repository.save(history1);
        SearchHistory saved = repository.findAllByUserIdRecent10(history1.getUserId()).get(0);

        LocalDateTime now = LocalDateTime.now();
        repository.updateTime(saved.getId(), now);
        SearchHistory updated = repository.findAllByUserIdRecent10(history1.getUserId()).get(0);

        assertThat(saved.getAccessTime()).isNotEqualTo(now);
//        assertThat(updated.getAccessTime()).isEqualTo(now);
    }
}