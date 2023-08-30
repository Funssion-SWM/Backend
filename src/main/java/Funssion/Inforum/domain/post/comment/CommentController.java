package Funssion.Inforum.domain.post.comment;

import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.response.IsSuccessResponseDto;
import Funssion.Inforum.domain.post.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comments")
    public IsSuccessResponseDto createComment(@RequestBody CommentSaveDto commentSaveDto){
        log.info("commentsavedto = {}",commentSaveDto.getCommentText());
        return commentService.createComment(commentSaveDto);
    }
}
