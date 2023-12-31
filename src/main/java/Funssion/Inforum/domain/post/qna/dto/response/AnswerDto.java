package Funssion.Inforum.domain.post.qna.dto.response;

import Funssion.Inforum.domain.post.qna.domain.Answer;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class AnswerDto {

    private Long id;
    private Long authorId;
    private String authorName;
    private String authorImagePath;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long likes;
    private Long dislikes;
    private String text;
    private Long questionId;
    private boolean isSelected;
    private Long repliesCount;
    private boolean isLike;
    private boolean isDisLike;
    private boolean isMine;
    private String rank;

    public AnswerDto(Answer answer,Long loginId){
        this.id = answer.getId();
        this.authorId = answer.getAuthorId();
        this.authorImagePath = answer.getAuthorImagePath();
        this.authorName = answer.getAuthorName();
        this.createdDate = answer.getCreatedDate();
        this.updatedDate = answer.getUpdatedDate();
        this.likes = answer.getLikes();
        this.dislikes =answer.getDislikes();
        this.text = answer.getText();
        this.repliesCount = answer.getRepliesCount();
        this.questionId = answer.getQuestionId();
        this.isSelected = answer.isSelected();
        this.isLike = answer.isLike();
        this.isDisLike = answer.isDisLike();
        this.isMine = loginId.equals(this.authorId);
        this.rank = answer.getRank();
    }
}
