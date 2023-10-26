package Funssion.Inforum.domain.professionalprofile.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode
public class ProfessionalProfile {
    private final Long userId;
    private final String introduce;
    private final String techStack;
    private final String answer1;
    private final String answer2;
    private final String answer3;
    private final String description;
    private final String resume;
}
