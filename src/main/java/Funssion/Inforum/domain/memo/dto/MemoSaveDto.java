package Funssion.Inforum.domain.memo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // cannot deserialize from object value (no delegate- or property-based creator) 오류 해결 위한 빈 생성자 생성
public class MemoSaveDto {
    @NotBlank(message = "제목을 입력해주세요")
    private String memoTitle;
    private String memoDescription;
    @NotBlank(message = "내용을 입력해주세요")
    private String memoText;
    @NotBlank(message = "색을 지정해주세요")
    private String memoColor;

    public MemoSaveDto(String memoTitle,String memoText, String memoColor) {
        this.memoTitle = memoTitle;
        this.memoText = memoText;
        this.memoColor = memoColor;
    }

    public MemoSaveDto(String memoTitle, String memoDescription, String memoText, String memoColor) {
        this.memoTitle = memoTitle;
        this.memoDescription = memoDescription;
        this.memoText = memoText;
        this.memoColor = memoColor;
    }
}
