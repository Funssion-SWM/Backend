package Funssion.Inforum.domain.post.searchhistory.dto.response;

import Funssion.Inforum.domain.post.searchhistory.domain.SearchHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class SearchHistoryDto {

    private final Long id;
    private final String searchText;
    private final Boolean isTag;

    public static SearchHistoryDto of(SearchHistory history) {
        return SearchHistoryDto.builder()
                .id(history.getUserId())
                .searchText(history.getSearchText())
                .isTag(history.getIsTag())
                .build();
    }
}
