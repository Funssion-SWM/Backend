package Funssion.Inforum.domain.post.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class QuestionSaveDto{
    @NotBlank(message="제목을 입력해주세요")
    private String title;
    @NotBlank(message="내용을 입력해주세요")
    private String text;
    private String description;
    private List<String> tags;
    @Builder
    public QuestionSaveDto(String title, String text, String description, List<String> tags) {
        this.title = title;
        this.text = text;
        this.description = description;
        this.tags = tags;
    }

    public static QuestionSaveDto tempQuestionDto() {
        return QuestionSaveDto.builder()
                .title("temporal question")
                .build();
    }

//    public QuestionSaveDto(Long authorId, MemberProfileEntity authorProfile, LocalDateTime createdDate, LocalDateTime updatedDate, String title, String text, List<String> tags) {
//        super(authorId, authorProfile, createdDate, updatedDate);
//        this.title = title;
//        this.text = text;
//        this.tags = tags;
//    }
}
