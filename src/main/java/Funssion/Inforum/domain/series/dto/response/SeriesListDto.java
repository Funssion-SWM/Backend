package Funssion.Inforum.domain.series.dto.response;

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
}
