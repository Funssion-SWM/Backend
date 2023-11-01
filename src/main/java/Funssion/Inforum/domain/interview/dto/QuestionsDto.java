package Funssion.Inforum.domain.interview.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsDto {
    @NotEmpty
    private String question1;
    @NotEmpty
    private String question2;
    @NotEmpty
    private String question3;
}
