package Funssion.Inforum.domain.professionalprofile.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CreateProfessionalProfileDto {
    private final String introduce;
    private final String techStack;
    private final String description;
    private final String answer1;
    private final String answer2;
    private final String answer3;
    private final String resume;
}
