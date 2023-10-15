package Funssion.Inforum.domain.series.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class MemoMetaInfoInSeries {
    private final Long id;
    private final String title;
}
