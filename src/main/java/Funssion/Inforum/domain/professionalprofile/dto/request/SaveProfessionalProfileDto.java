package Funssion.Inforum.domain.professionalprofile.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveProfessionalProfileDto {
    @Size(max = 500, message = "한 줄 자기소개는 500자를 초과할 수 없습니다")
    private String introduce;
    @Size(max = 50)
    private String developmentArea;
    @Size(max = 1000)
    private String techStack;
    @Size(max = 500)
    private String description;
    @Size(max = 500, message = "질문 1에 대한 답변은 500자를 초과할 수 없습니다")
    private String answer1;
    @Size(max = 500, message = "질문 2에 대한 답변은 500자를 초과할 수 없습니다")
    private String answer2;
    @Size(max = 500, message = "질문 3에 대한 답변은 500자를 초과할 수 없습니다")
    private String answer3;
    @Size(max = 50000)
    private String resume;
}
