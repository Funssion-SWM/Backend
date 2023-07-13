package Funssion.Inforum.swagger.memo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemoCreateDataForm {
    @Schema(description = "메모 제목" , example = "JPA는 바로바로...")
    private String memoTitle;
    @Schema(description = "메모 내용" , example = "JPA이다")
    private String memoText;
    @Schema(description = "메모 색깔" , example = "green")
    private String memoColor;

    public MemoCreateDataForm(String memoTitle, String memoText, String memoColor) {
        this.memoTitle = memoTitle;
        this.memoText = memoText;
        this.memoColor = memoColor;
    }
}
