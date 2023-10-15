package Funssion.Inforum.domain.post.domain;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
@ToString
@EqualsAndHashCode(exclude = {"id", "createdDate"})
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Long id;
    private Long authorId;
    private String authorName;
    private String authorImagePath;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long likes;

    public Post(Long authorId, MemberProfileEntity authorProfile, LocalDateTime createdDate, LocalDateTime updatedDate) {
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
