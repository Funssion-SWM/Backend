package Funssion.Inforum.domain.series.dto.response;

import Funssion.Inforum.domain.series.domain.Series;
import Funssion.Inforum.domain.series.dto.MemoMetaInfoInSeries;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SeriesResponseDto {
    private final Long id;
    private List<MemoMetaInfoInSeries> memoInfoList;
    private final Long likes;
    private final LocalDateTime created;

    @Builder
    public SeriesResponseDto(Long id, Long likes, LocalDateTime created) {
        this.id = id;
        this.likes = likes;
        this.created = created;
    }

    public static SeriesResponseDto valueOf(Series series) {
        return SeriesResponseDto.builder()
                .id(series.getId())
                .likes(series.getLikes())
                .created(series.getCreatedDate())
                .build();
    }

    public void setMemoMetaInfo(List<MemoMetaInfoInSeries> memoMetaInfoInSeriesList) {
        this.memoInfoList = List.copyOf(memoMetaInfoInSeriesList);
    }
}
