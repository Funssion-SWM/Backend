package Funssion.Inforum.swagger.memo.entity;


import Funssion.Inforum.swagger.memo.form.MemoCreateDataForm;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonAutoDetect
public class MemoEntity {
    private int memoId;
    private int userId;
    private String userName;
    private String memoTitle;
    private String memoText;
    private String memoColor;
    private LocalDate createdDate;
    private LocalDate updatedDate;

    public MemoEntity(int memoId, int userId, String userName, String memoTitle, String memoText, String memoColor, LocalDate createdDate, LocalDate updatedDate) {
        this.memoId = memoId;
        this.userId = userId;
        this.userName = userName;
        this.memoTitle = memoTitle;
        this.memoText = memoText;
        this.memoColor = memoColor;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
