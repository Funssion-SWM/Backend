package Funssion.Inforum.domain.post.searchhistory.service;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.searchhistory.domain.SearchHistory;
import Funssion.Inforum.domain.post.searchhistory.dto.response.SearchHistoryDto;
import Funssion.Inforum.domain.post.searchhistory.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public List<SearchHistoryDto> getRecentSearchHistoryTop10() {
        Long userId = SecurityContextUtils.getUserId();

        return searchHistoryRepository.findAllByUserIdRecent10(userId).stream()
                .map(searchHistory -> SearchHistoryDto.of(searchHistory))
                .toList();
    }

    @Transactional
    public void addSearchHistory(String searchString, Boolean isTag) {
        Long userId = SecurityContextUtils.getUserId();

        searchHistoryRepository.save(SearchHistory.builder()
                        .searchText(searchString)
                        .isTag(isTag)
                        .userId(userId)
                        .accessTime(LocalDateTime.now())
                        .build());
    }



    @Transactional
    public void removeSearchHistory(Long id) {
        searchHistoryRepository.delete(id);
    }

    @Transactional
    public void refreshSearchHistory(Long id) {
        searchHistoryRepository.updateTime(id, LocalDateTime.now());
    }
}
