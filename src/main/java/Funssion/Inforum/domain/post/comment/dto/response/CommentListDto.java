package Funssion.Inforum.domain.post.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Getter
@Builder
public class CommentListDto {
    private Long id;
    private Long authorId;
    private String authorName;
    private String authorImagePath;
    private String commentText;
    private Date createdDate;
    private Date updatedDate;
    private Long likes;
    private Long reComments;

}
