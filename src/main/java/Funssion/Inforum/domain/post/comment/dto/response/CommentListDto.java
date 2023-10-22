package Funssion.Inforum.domain.post.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder

public class CommentListDto {
    private Long id;
    private Long authorId;
    private String authorName;
    private String authorImagePath;
    private String authorRank;
    private String commentText;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long likes;
    private Long reCommentsNumber;
    private Boolean isLike;
    private Boolean isUserDelete;

}
