package Funssion.Inforum.domain.post.qna.domain;

import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.domain.Post;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Question extends Post {
    private final String title;
    private final String description;
    private final String text;
    private final List<String> tags;
    private final Long memoId;
    private final Long answersCount;
    private final boolean isSolved;

    public Question(Long authorId, MemberProfileEntity authorProfile, LocalDateTime createdDate, LocalDateTime updatedDate, String title, String description, String text, List<String> tags, Long answersCount, boolean isSolved, Long memoId) {
        super(authorId, authorProfile, createdDate, updatedDate);
        this.title = title;
        this.description = description;
        this.text = text;
        this.tags = tags;
        this.memoId = memoId;
        this.answersCount = answersCount;
        this.isSolved = isSolved;
    }
}
