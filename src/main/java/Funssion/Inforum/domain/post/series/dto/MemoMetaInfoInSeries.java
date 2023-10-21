package Funssion.Inforum.domain.post.series.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class MemoMetaInfoInSeries {
    private final Long id;
    private final String title;
    private final String color;
}
