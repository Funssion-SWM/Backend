package Funssion.Inforum.domain.profile.dto.response;

import lombok.*;

@Getter
@RequiredArgsConstructor
@Builder
public class UserProfileForEmployer {

    private final Long id;
    private final String name;
    private final String imagePath;
    private final String rank;
    private final String introduce;
    private final String developmentArea;
    private final String techStack;
    private final String description;
    private final Boolean isLike;
}
