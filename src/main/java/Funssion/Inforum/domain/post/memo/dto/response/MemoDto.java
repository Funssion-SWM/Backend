package Funssion.Inforum.domain.post.memo.dto.response;

import Funssion.Inforum.domain.post.memo.domain.Memo;
import lombok.Getter;

import java.sql.Date;

@Getter
public class MemoDto {
    private Long memoId;
    private Long authorId;
    private String authorName;
    private String authorImage;
    private String memoTitle;
    private String memoDescription;
    private String memoText;
    private String memoColor;
    private Date createdDate;
    private Date updatedDate;
    private Long likes;
    private Boolean isLike;

    public MemoDto(Memo memo, String authorName) {
        this.memoId = memo.getId();
        this.authorId = memo.getAuthorId();
        this.authorName = authorName;
        this.memoTitle = memo.getTitle();
        this.memoDescription = memo.getDescription();
        this.memoText = memo.getText();
        this.memoColor = memo.getColor();
        this.createdDate = memo.getCreatedDate();
        this.updatedDate = memo.getUpdatedDate();
        this.likes = memo.getLikes();
    }

    public void setIsLike(Boolean isLike) {
        this.isLike = isLike;
    }
}
