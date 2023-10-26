package Funssion.Inforum.domain.professionalprofile.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UpdateResumeDto {
    private final String resume;
}
