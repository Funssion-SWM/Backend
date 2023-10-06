package Funssion.Inforum.domain.post.qna.dto.response;

import Funssion.Inforum.domain.post.qna.domain.Question;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class QuestionDto {
    @Builder
    public QuestionDto(Long id, Long authorId, String authorName, String authorImagePath, String description, LocalDateTime createdDate, LocalDateTime updatedDate, Long likes, String title, String text, Long likesCount, Long answersCount, Long memoId, boolean isSolved, List<String> tags, boolean isMine) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorImagePath = authorImagePath;
        this.description = description;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.likes = likes;
        this.title = title;
        this.text = text;
        this.likesCount = likesCount;
        this.answersCount = answersCount;
        this.memoId = memoId;
        this.isSolved = isSolved;
        this.tags = tags;
        this.isMine = isMine;
    }

    private Long id;
    private Long authorId;
    private String authorName;
    private String authorImagePath;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long likes;
    private String title;
    private String text;
    private Long likesCount;
    private Long repliesCount;
    private Long answersCount;
    private Long memoId;
    private boolean isSolved;
    private List<String> tags;
    private boolean isMine;
    private boolean isLike;
    public QuestionDto(Question question, Long authorId) {
        this.id = question.getId();
        this.authorId = question.getAuthorId();
        this.authorName = question.getAuthorName();
        this.authorImagePath = question.getAuthorImagePath();
        this.description = question.getDescription();
        this.createdDate = question.getCreatedDate();
        this.updatedDate = question.getUpdatedDate();
        this.likes = question.getLikes();
        this.title = question.getTitle();
        this.text = question.getText();
        this.tags = question.getTags();
        this.likesCount = question.getLikes();
        this.answersCount = question.getAnswersCount();
        this.repliesCount = question.getRepliesCount();
        this.isSolved = question.isSolved();
        this.memoId = question.getMemoId();
        this.isMine = this.authorId.equals(authorId);
    }
}
