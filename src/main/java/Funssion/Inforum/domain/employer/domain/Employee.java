package Funssion.Inforum.domain.employer.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class Employee {
    private final Long userId;
    private final String username;
    private final String imagePath;
    private final String rank;
    private final Long score;
    private final Boolean isLike;
}
