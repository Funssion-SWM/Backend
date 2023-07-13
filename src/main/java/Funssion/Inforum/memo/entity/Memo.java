package Funssion.Inforum.memo.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.sql.Date;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Memo {
    private int memoId;
    private int userId;
    private String userName;
    private String memoTitle;
    private String memoText;
    private String memoColor;
    private Date createdDate;
    private Date updatedDate;

    public Memo(int userId, String userName, String memoTitle, String memoText, String memoColor, Date createdDate, Date updatedDate) {
        this.userId = userId;
        this.userName = userName;
        this.memoTitle = memoTitle;
        this.memoText = memoText;
        this.memoColor = memoColor;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public Memo(int memoId, int userId, String userName, String memoTitle, String memoText, String memoColor, Date createdDate, Date updatedDate) {
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