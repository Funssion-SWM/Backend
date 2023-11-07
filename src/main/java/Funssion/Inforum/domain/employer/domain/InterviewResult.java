package Funssion.Inforum.domain.employer.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class InterviewResult {
    private final String question1;
    private final String answer1;
    private final String question2;
    private final String answer2;
    private final String question3;
    private final String answer3;
}
