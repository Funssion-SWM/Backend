package Funssion.Inforum.domain.post.domain;

import Funssion.Inforum.common.constant.Sign;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Getter
@SuperBuilder
@EqualsAndHashCode
@AllArgsConstructor
public class Post {
    private Long id;
    private long authorId;
    private Date createdDate;
    private Date updatedDate;
    private long likes;

    public Post(Long authorId, Date createdDate, Date updatedDate) {
        this.authorId = authorId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public Post(Long id, Long authorId, Date updatedDate) {
        this.id = id;
        this.authorId = authorId;
        this.updatedDate = updatedDate;
    }

    public void setIdForTest(Long id) {
        this.id = id;
    }

    public void updateLikes(Sign sign) {
        switch (sign) {
            case PLUS -> likes++;
            case MINUS -> likes--;
        }
    }
}
