package Funssion.Inforum.domain.post.memo.domain;

import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.domain.Post;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Memo extends Post {
    private String title;
    private String description;
    private String text;
    private String color;
    private List<String> memoTags;
    private Boolean isTemporary;


    public Memo(MemoSaveDto form, Long authorId, MemberProfileEntity authorProfile, LocalDateTime createdDate, LocalDateTime updatedDate) {
        super(authorId, authorProfile, createdDate, updatedDate);
        this.title = form.getMemoTitle();
        this.description = form.getMemoDescription();
        this.text = form.getMemoText();
        this.color = form.getMemoColor();
        this.memoTags = form.getMemoTags();
        this.isTemporary = form.getIsTemporary();
    }

    public Memo(MemoSaveDto form) {
        this.title = form.getMemoTitle();
        this.description = form.getMemoDescription();
        this.text = form.getMemoText();
        this.color = form.getMemoColor();
        this.memoTags = form.getMemoTags();
        this.isTemporary = form.getIsTemporary();
    }
}