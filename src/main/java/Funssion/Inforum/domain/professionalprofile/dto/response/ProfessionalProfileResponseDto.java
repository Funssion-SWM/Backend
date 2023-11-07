package Funssion.Inforum.domain.professionalprofile.dto.response;

import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ProfessionalProfileResponseDto {
    private final Long userId;
    private final String introduce;
    private final String developmentArea;
    private final String techStack;
    private final String answer1;
    private final String answer2;
    private final String answer3;
    private final String description;
    private final String resume;

    public static ProfessionalProfileResponseDto valueOf(ProfessionalProfile professionalProfile) {
        return ProfessionalProfileResponseDto.builder()
                .userId(professionalProfile.getUserId())
                .introduce(professionalProfile.getIntroduce())
                .developmentArea(professionalProfile.getDevelopmentArea())
                .techStack(professionalProfile.getTechStack())
                .description(professionalProfile.getDescription())
                .answer1(professionalProfile.getAnswer1())
                .answer2(professionalProfile.getAnswer2())
                .answer3(professionalProfile.getAnswer3())
                .resume(professionalProfile.getResume())
                .build();
    }
}
