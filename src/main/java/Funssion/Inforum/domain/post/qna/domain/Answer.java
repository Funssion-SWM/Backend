package Funssion.Inforum.domain.post.qna.domain;

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
    public Answer(Long authorId, MemberProfileEntity authorProfile, LocalDateTime createdDate, LocalDateTime updatedDate, String description, String text, Long questionId, boolean isSelected, Long repliesCount) {
        super(authorId, authorProfile, createdDate, updatedDate);
        this.description = description;
        this.text = text;
        this.questionId = questionId;
        this.isSelected = isSelected;
        this.repliesCount = repliesCount;
    }

    private final String description;
    private final String text;
    private final Long questionId;
    private final boolean isSelected;
    private final Long repliesCount;
}
