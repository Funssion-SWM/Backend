package Funssion.Inforum.domain.post.series.dto.response;

import Funssion.Inforum.domain.post.series.domain.Series;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
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
    private List<String> topThreeColors;

    @Builder
    public SeriesListDto(Long id, Long authorId, String authorName, String authorProfileImagePath, String title, String description, String thumbnailImagePath, Long likes, LocalDateTime created) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorProfileImagePath = authorProfileImagePath;
        this.title = title;
        this.description = description;
        this.thumbnailImagePath = thumbnailImagePath;
        this.likes = likes;
        this.created = created;
    }

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

    public void setTopThreeColors(List<String> topThreeColors) {
        this.topThreeColors = topThreeColors;
    }
}
