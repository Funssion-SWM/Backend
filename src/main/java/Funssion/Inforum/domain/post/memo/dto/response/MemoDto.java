package Funssion.Inforum.domain.post.memo.dto.response;

import Funssion.Inforum.domain.post.memo.domain.Memo;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MemoDto {
    private Long memoId;
    private Long authorId;
    private String authorName;
    private String authorProfileImagePath;
    private String memoTitle;
    private String memoDescription;
    private String memoText;
    private String memoColor;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long likes;
    private List<String> memoTags;
    private Boolean isTemporary;
    private Boolean isMine;

    public MemoDto(Memo memo) {
        this.memoId = memo.getId();
        this.authorId = memo.getAuthorId();
        this.authorName = memo.getAuthorName();
        this.authorProfileImagePath = memo.getAuthorImagePath();
        this.memoTitle = memo.getTitle();
        this.memoDescription = memo.getDescription();
        this.memoText = memo.getText();
        this.memoColor = memo.getColor();
        this.createdDate = memo.getCreatedDate();
        this.updatedDate = memo.getUpdatedDate();
        this.memoTags = memo.getMemoTags();
        this.likes = memo.getLikes();
        this.isTemporary = memo.getIsTemporary();
    }

    public void setIsMine(Long authorId) {
        this.isMine = this.authorId.equals(authorId);
    }
}
