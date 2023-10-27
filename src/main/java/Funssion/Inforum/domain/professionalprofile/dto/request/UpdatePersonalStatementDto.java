package Funssion.Inforum.domain.professionalprofile.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePersonalStatementDto {
    @NotBlank(message = "한줄 자기소개는 필수 입력 사항입니다.")
    private String introduce;
    @NotBlank(message = "기술 스택은 필수 입력 사항입니다.")
    private String techStack;
    private String description;
    @NotBlank(message = "질문 1에 대한 답변을 작성해주세요.")
    private String answer1;
    @NotBlank(message = "질문 2에 대한 답변을 작성해주세요.")
    private String answer2;
    @NotBlank(message = "질문 3에 대한 답변을 작성해주세요.")
    private String answer3;
}
