package Funssion.Inforum.domain.post.qna.domain;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.domain.Post;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Answer extends Post {
    public Answer(Long authorId, MemberProfileEntity authorProfile, LocalDateTime createdDate, LocalDateTime updatedDate, String text, Long questionId, Long dislikes, boolean isSelected, Long repliesCount) {
        super(authorId, authorProfile, createdDate, updatedDate);
        this.text = text;
        this.questionId = questionId;
        this.dislikes = dislikes;
        this.isSelected = isSelected;
        this.repliesCount = repliesCount;
    }

    private final String text;
    private final Long questionId;
    private final boolean isSelected;
    private final Long repliesCount;

    private Long dislikes;
    private Long authorScore;
    private boolean isLike;
    private boolean isDisLike;

    public Long updateDisLikes(Sign sign) {
        switch (sign) {
            case PLUS -> dislikes++;
            case MINUS -> dislikes--;
        }
        return dislikes;
    }
    public Answer setScoreInfo(Long authorScore){
        this.authorScore = authorScore;
        return this;
    }
}
