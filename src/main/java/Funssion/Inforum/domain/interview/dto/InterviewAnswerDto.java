package Funssion.Inforum.domain.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewAnswerDto {
    private Long employerId;
    private Integer questionNumber;
    private String answer;
}
