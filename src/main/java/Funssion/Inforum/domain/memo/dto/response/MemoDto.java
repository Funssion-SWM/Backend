package Funssion.Inforum.domain.memo.dto.response;

import Funssion.Inforum.domain.memo.entity.Memo;
import lombok.Getter;

import java.sql.Date;

@Getter
public class MemoDto {
    private Long memoId;
    private Long authorId;
    private String authorName;
    private String memoTitle;
    private String memoDescription;
    private String memoText;
    private String memoColor;
    private Date createdDate;
    private Date updatedDate;

    public MemoDto(Memo memo) {
        this.memoId = memo.getMemoId();
        this.authorId = memo.getAuthorId();
        this.authorName = memo.getAuthorName();
        this.memoTitle = memo.getMemoTitle();
        this.memoDescription = memo.getMemoDescription();
        this.memoText = memo.getMemoText();
        this.memoColor = memo.getMemoColor();
        this.createdDate = memo.getCreatedDate();
        this.updatedDate = memo.getUpdatedDate();
    }
}
