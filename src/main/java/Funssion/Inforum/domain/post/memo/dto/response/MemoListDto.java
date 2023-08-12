package Funssion.Inforum.domain.post.memo.dto.response;

import Funssion.Inforum.domain.post.memo.domain.Memo;
import lombok.Getter;

import java.sql.Date;

@Getter
public class MemoListDto {
    private Long memoId;
    private String memoTitle;
    private String memoText;
    private String memoDescription;
    private String memoColor;
    private Date createdDate;
    private Long authorId;
    private Long likes;

    public MemoListDto(Memo memo) {
        this.memoId = memo.getId();
        this.memoTitle = memo.getTitle();
        this.memoText = memo.getText();
        this.memoDescription = memo.getDescription();
        this.memoColor = memo.getColor();
        this.createdDate = memo.getCreatedDate();
        this.authorId = memo.getAuthorId();
        this.likes = memo.getLikes();
    }
}
