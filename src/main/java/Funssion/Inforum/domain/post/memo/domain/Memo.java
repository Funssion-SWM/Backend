package Funssion.Inforum.domain.post.memo.domain;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.post.domain.Post;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;


@Getter
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = true)
public class Memo extends Post {
    private String title;
    private String description;
    private String text;
    private String color;

    public Memo(MemoSaveDto form, Long authorId, String authorName, Date createdDate, Date updatedDate) {
        super(authorId, authorName, createdDate, updatedDate);
        this.title = form.getMemoTitle();
        this.description = form.getMemoDescription();
        this.text = form.getMemoText();
        this.color = form.getMemoColor();
    }

    public Memo(MemoSaveDto form, Long memoId, Long authorId, Date updatedDate) {
        super(memoId, authorId, updatedDate);
        this.title = form.getMemoTitle();
        this.description = form.getMemoDescription();
        this.text = form.getMemoText();
        this.color = form.getMemoColor();
    }
}