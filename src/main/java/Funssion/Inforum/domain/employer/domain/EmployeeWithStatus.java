package Funssion.Inforum.domain.employer.domain;

import Funssion.Inforum.domain.interview.constant.InterviewStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class EmployeeWithStatus {
    private final Long userId;
    private final String username;
    private final String email;
    private final String imagePath;
    private final String rank;
    private final String introduce;
    private final String developmentArea;
    private final String description;
    private final String techStack;
    private final Boolean isVisible;
    private final InterviewStatus status;
}
