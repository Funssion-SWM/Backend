package Funssion.Inforum.memo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemoSaveDto {
    @NotBlank(message = "제목을 입력해주세요")
    private String memoTitle;
    @NotBlank(message = "내용을 입력해주세요")
    private String memoText;
    @NotBlank(message = "색을 지정해주세요")
    private String memoColor;

    public MemoSaveDto(String memoTitle, String memoText, String memoColor) {
        this.memoTitle = memoTitle;
        this.memoText = memoText;
        this.memoColor = memoColor;
    }
}
