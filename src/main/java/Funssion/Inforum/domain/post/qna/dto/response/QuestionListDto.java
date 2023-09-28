package Funssion.Inforum.domain.post.qna.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class QuestionListDto {
    private Long id;
    private String title;
    private String text;
    private LocalDateTime createdDate;
    private Long authorId;
    private String authorName;
    private String authorProfileImagePath;
    private Long answersCount;
    private Long likes;
    private Boolean isLike;
    private List<String> tags;
}
