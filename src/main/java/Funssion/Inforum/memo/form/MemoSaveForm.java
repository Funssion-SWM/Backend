package Funssion.Inforum.memo.form;

import lombok.Data;

@Data
public class MemoSaveForm {
    private String memoTitle;
    private String memoText;
    private String memoColor;

    public MemoSaveForm(String memoTitle, String memoText, String memoColor) {
        this.memoTitle = memoTitle;
        this.memoText = memoText;
        this.memoColor = memoColor;
    }
}
