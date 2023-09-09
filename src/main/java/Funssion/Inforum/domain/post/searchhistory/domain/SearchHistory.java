package Funssion.Inforum.domain.post.searchhistory.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
@RequiredArgsConstructor
public class SearchHistory {

    private final Long id;
    private final Long userId;
    private final String searchText;
    private final Boolean isTag;
    private final LocalDateTime accessTime;
}
