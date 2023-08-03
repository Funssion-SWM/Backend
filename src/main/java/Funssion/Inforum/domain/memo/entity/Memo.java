package Funssion.Inforum.domain.memo.entity;

import Funssion.Inforum.domain.memo.dto.request.MemoSaveDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.sql.Date;


@Getter
@Builder
@ToString
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@EqualsAndHashCode
public class Memo {
    private int memoId;
    private int authorId;
    private String authorName;
    private String memoTitle;
    private String memoDescription;
    private String memoText;
    private String memoColor;
    private Date createdDate;
    private Date updatedDate;

    public Memo(MemoSaveDto form, Integer authorId, String authorName, Date createdDate, Date updatedDate) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.memoTitle = form.getMemoTitle();
        this.memoDescription = form.getMemoDescription();
        this.memoText = form.getMemoText();
        this.memoColor = form.getMemoColor();
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public Memo(MemoSaveDto form, Integer memoId, Date updatedDate) {
        this.memoId = memoId;
        this.memoTitle = form.getMemoTitle();
        this.memoDescription = form.getMemoDescription();
        this.memoText = form.getMemoText();
        this.memoColor = form.getMemoColor();
        this.updatedDate = updatedDate;
    }

    public void setMemoIdForTest(Integer memoId) {
        this.memoId = memoId;
    }
}