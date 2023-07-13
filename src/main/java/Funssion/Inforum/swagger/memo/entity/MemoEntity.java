package Funssion.Inforum.swagger.memo.entity;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonAutoDetect
public class MemoEntity {
    private int memoId;
    private String memoTitle;
    private String memoText;
    private String memoColor;
    private int authorId;
    private String authorName;
    private LocalDate createdDate;
    private LocalDate updatedDate;

    public MemoEntity(int memoId, String memoTitle, String memoText, String memoColor, int authorId, String authorName, LocalDate createdDate, LocalDate updatedDate) {
        this.memoId = memoId;
        this.memoTitle = memoTitle;
        this.memoText = memoText;
        this.memoColor = memoColor;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
