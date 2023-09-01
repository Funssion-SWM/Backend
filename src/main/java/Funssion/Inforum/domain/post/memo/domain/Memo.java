package Funssion.Inforum.domain.post.memo.domain;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.post.domain.Post;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;


@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Memo extends Post {
    private String title;
    private String description;
    private String text;
    private String color;
    private Boolean isTemporary;

    public Memo(MemoSaveDto form, Long authorId, MemberProfileEntity authorProfile, Date createdDate, Date updatedDate) {
        super(authorId, authorProfile, createdDate, updatedDate);
        this.title = form.getMemoTitle();
        this.description = form.getMemoDescription();
        this.text = form.getMemoText();
        this.color = form.getMemoColor();
        this.isTemporary = form.getIsTemporary();
    }

    public Memo(MemoSaveDto form) {
        this.title = form.getMemoTitle();
        this.description = form.getMemoDescription();
        this.text = form.getMemoText();
        this.color = form.getMemoColor();
        this.isTemporary = form.getIsTemporary();
    }
}