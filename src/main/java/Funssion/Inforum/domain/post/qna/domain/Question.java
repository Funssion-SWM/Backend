package Funssion.Inforum.domain.post.qna.domain;

import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.domain.Post;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Question extends Post {
    private final String title;
    private final String text;
    private final List<String> tags;

    public Question(Long authorId, MemberProfileEntity authorProfile, LocalDateTime createdDate, LocalDateTime updatedDate, String title, String text, List<String> tags) {
        super(authorId, authorProfile, createdDate, updatedDate);
        this.title = title;
        this.text = text;
        this.tags = tags;
    }
}
