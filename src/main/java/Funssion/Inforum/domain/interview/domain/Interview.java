package Funssion.Inforum.domain.interview.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class Interview {
    private final Long employerId;
    private final String status;
    private final String question1;
    private final String question2;
    private final String question3;
}
