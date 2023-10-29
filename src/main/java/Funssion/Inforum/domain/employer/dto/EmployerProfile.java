package Funssion.Inforum.domain.employer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class EmployerProfile {
    private final Long employerId;
    private final String companyName;
    private final String nickname;
    private final String imagePath;
    private final String rank;
}
