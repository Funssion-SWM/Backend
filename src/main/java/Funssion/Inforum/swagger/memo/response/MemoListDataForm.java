package Funssion.Inforum.swagger.memo.response;

import java.time.LocalDate;

public class MemoListDataForm {
    private int memoId;
    private String memoTitle;
    private String memoText;
    private String memoColor;
    private LocalDate createdDate;
    private String authorId;

    public MemoListDataForm(int memoId, String memoTitle, String memoText, String memoColor, LocalDate createdDate, String authorId, String authorName) {
        this.memoId = memoId;
        this.memoTitle = memoTitle;
        this.memoText = memoText;
        this.memoColor = memoColor;
        this.createdDate = createdDate;
        this.authorId = authorId;
        this.authorName = authorName;
    }

    private String authorName;

}
