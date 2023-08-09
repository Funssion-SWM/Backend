package Funssion.Inforum.domain.member.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class IsProfileSavedDto {
    private final Boolean isSuccess;
    private final String image_path;
    private final String tags;
    private final String introduce;
}
