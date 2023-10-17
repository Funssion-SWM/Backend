package Funssion.Inforum.domain.post.memo.dto.response;

import Funssion.Inforum.domain.post.memo.domain.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class MemoListDto {
    private Long memoId;
    private String memoTitle;
    private String memoText;
    private String memoDescription;
    private String memoColor;
    private LocalDateTime createdDate;
    private Long authorId;
    private String authorName;
    private String authorProfileImagePath;
    private Long repliesCount;
    private Long questionCount;
    private Long likes;
    private Boolean isLike;
    private List<String> memoTags;
    private Boolean isTemporary;
    private Long seriesId;


    public MemoListDto(Memo memo) {
        this.memoId = memo.getId();
        this.memoTitle = memo.getTitle();
        this.memoText = memo.getText();
        this.memoDescription = memo.getDescription();
        this.memoColor = memo.getColor();
        this.createdDate = memo.getCreatedDate();
        this.authorId = memo.getAuthorId();
        this.authorName = memo.getAuthorName();
        this.authorProfileImagePath = memo.getAuthorImagePath();
        this.repliesCount = memo.getRepliesCount();
        this.likes = memo.getLikes();
        this.memoTags = memo.getMemoTags();
        this.isTemporary = memo.getIsTemporary();
        this.questionCount = memo.getQuestionCount();
        this.seriesId = memo.getSeriesId();

        setMemoTagsLimit(2);
    }

    private void setMemoTagsLimit(Integer count) {
        int listSize = Integer.min(this.memoTags.size(), count);
        this.memoTags = this.memoTags.subList(0, listSize);
    }

    public void setIsLike(Boolean isLike) {
        this.isLike = isLike;
    }
}
