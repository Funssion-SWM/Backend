package Funssion.Inforum.domain.post.searchhistory.service;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.searchhistory.dto.response.SearchHistoryDto;
import Funssion.Inforum.domain.post.searchhistory.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public List<SearchHistoryDto> getSearchHistories() {
        Long userId = SecurityContextUtils.getUserId();

        return searchHistoryRepository.findSearchHistoryListByUserId(userId).stream()
                .map(searchHistory -> SearchHistoryDto.of(searchHistory))
                .toList();
    }
}
