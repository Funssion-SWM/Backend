package Funssion.Inforum.domain.series.dto.response;

import Funssion.Inforum.domain.series.domain.Series;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class SeriesListDto {
    private final Long id;
    private final Long authorId;
    private final String authorName;
    private final String authorProfileImagePath;
    private final String title;
    private final String description;
    private final String thumbnailImagePath;
    private final Long likes;
    private final LocalDateTime created;

    public static SeriesListDto valueOf(Series series) {
        return SeriesListDto.builder()
                .id(series.getId())
                .authorId(series.getAuthorId())
                .authorName(series.getAuthorName())
                .authorProfileImagePath(series.getAuthorImagePath())
                .title(series.getTitle())
                .description(series.getDescription())
                .thumbnailImagePath(series.getThumbnailImagePath())
                .likes(series.getLikes())
                .created(series.getCreatedDate())
                .build();
    }
}
