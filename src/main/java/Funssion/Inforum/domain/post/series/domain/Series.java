package Funssion.Inforum.domain.post.series.domain;

import Funssion.Inforum.domain.post.domain.Post;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Series extends Post {
    private final String title;
    private final String description;
    private final String thumbnailImagePath;
}
