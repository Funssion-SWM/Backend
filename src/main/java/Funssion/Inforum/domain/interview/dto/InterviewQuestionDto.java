package Funssion.Inforum.domain.interview.dto;

import Funssion.Inforum.domain.interview.domain.Interview;
import lombok.Getter;

@Getter
public class InterviewQuestionDto {
    private final String question1;
    private final String question2;
    private final String question3;
    private final String status;
    private final Long employerId;
    private String companyName;
    public InterviewQuestionDto(Interview interviewQuestion, String companyName){
        this.question1 = interviewQuestion.getQuestion1();
        this.question2 = interviewQuestion.getQuestion2();
        this.question3 = interviewQuestion.getQuestion3();
        this.status = interviewQuestion.getStatus();
        this.employerId = interviewQuestion.getEmployerId();
        this.companyName = companyName;

    }
}
