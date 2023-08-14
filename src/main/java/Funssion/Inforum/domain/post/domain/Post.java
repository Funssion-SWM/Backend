package Funssion.Inforum.domain.post.domain;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Getter
@SuperBuilder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Long id;
    private long authorId;
    private String authorName;
    private String authorImagePath;
    private Date createdDate;
    private Date updatedDate;
    private long likes;

    public Post(Long authorId, MemberProfileEntity authorProfile, Date createdDate, Date updatedDate) {
        this.authorId = authorId;
        this.authorName = authorProfile.getNickname();
        this.authorImagePath = authorProfile.getProfileImageFilePath();
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public Long updateLikes(Sign sign) {
        switch (sign) {
            case PLUS -> likes++;
            case MINUS -> likes--;
        }
        return likes;
    }
}
