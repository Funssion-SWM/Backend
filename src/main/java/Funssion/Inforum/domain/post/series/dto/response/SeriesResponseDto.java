package Funssion.Inforum.domain.post.series.dto.response;

import Funssion.Inforum.domain.post.series.domain.Series;
import Funssion.Inforum.domain.post.series.dto.MemoMetaInfoInSeries;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SeriesResponseDto {
    private final Long id;
    private final String title;
    private final String description;
    private final String thumbnailImagePath;
    private List<MemoMetaInfoInSeries> memoInfoList;
    private final Long likes;
    private final LocalDateTime created;

    @Builder
    public SeriesResponseDto(Long id, String title, String description, String thumbnailImagePath, Long likes, LocalDateTime created) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnailImagePath = thumbnailImagePath;
        this.likes = likes;
        this.created = created;
    }

    public static SeriesResponseDto valueOf(Series series) {
        return SeriesResponseDto.builder()
                .id(series.getId())
                .title(series.getTitle())
                .description(series.getDescription())
                .thumbnailImagePath(series.getThumbnailImagePath())
                .likes(series.getLikes())
                .created(series.getCreatedDate())
                .build();
    }

    public void setMemoMetaInfo(List<MemoMetaInfoInSeries> memoMetaInfoInSeriesList) {
        this.memoInfoList = List.copyOf(memoMetaInfoInSeriesList);
    }
}
