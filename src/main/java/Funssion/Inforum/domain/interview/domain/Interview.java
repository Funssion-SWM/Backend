package Funssion.Inforum.domain.interview.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@Builder
public class Interview {
    private final Long employerId;
    private final String status;
    private final String question1;
    private final String question2;
    private final String question3;
}
