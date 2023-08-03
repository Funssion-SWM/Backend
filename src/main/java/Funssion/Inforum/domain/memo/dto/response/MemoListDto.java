package Funssion.Inforum.domain.memo.dto.response;

import Funssion.Inforum.domain.memo.entity.Memo;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.sql.Date;

@Getter
public class MemoListDto {
    private int memoId;
    private String memoTitle;
    private String memoText;
    private String memoDescription;
    private String memoColor;
    private Date createdDate;
    private int authorId;
    private String authorName;

    public MemoListDto(Memo memo) {
        this.memoId = memo.getMemoId();
        this.memoTitle = memo.getMemoTitle();
        this.memoText = memo.getMemoText();
        this.memoDescription = memo.getMemoDescription();
        this.memoColor = memo.getMemoColor();
        this.createdDate = memo.getCreatedDate();
        this.authorId = memo.getAuthorId();
        this.authorName = memo.getAuthorName();
    }
}
