package Funssion.Inforum.domain.post.comment.controller;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.IsSuccessResponseDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @PostMapping()
    public IsSuccessResponseDto createComment(@RequestBody CommentSaveDto commentSaveDto){
        return commentService.createComment(commentSaveDto);
    }

    @PatchMapping("/{commentId}")
    public IsSuccessResponseDto updateComment(@RequestBody CommentUpdateDto commentUpdateDto, @PathVariable Long commentId){
        return commentService.updateComment(commentUpdateDto,commentId);
    }

    @DeleteMapping("/{commentId}")
    public IsSuccessResponseDto deleteComment(@PathVariable Long commentId){
        return commentService.deleteComment(commentId);
    }

    @GetMapping("/{postType}/{postId}")
    public List<CommentListDto> getCommentsAtPostIn(@PathVariable String postType, @PathVariable Long postId){
        return commentService.getCommentsAtPost(PostType.valueOf(postType.toUpperCase()),postId);
        /* To Do
            converter 사용할 것
         */
    }

    @PostMapping("/recomments")
    public IsSuccessResponseDto createReComment(@RequestBody ReCommentSaveDto reCommentSaveDto){
        return commentService.createReComment(reCommentSaveDto);
    }

    @PatchMapping("/recomments/{reCommentId}")
    public IsSuccessResponseDto updateReComment(@RequestBody ReCommentUpdateDto reCommentUpdateDto, @PathVariable Long reCommentId){
        return commentService.updateReComment(reCommentUpdateDto,reCommentId);
    }

    @DeleteMapping("/recomments/{reCommentId}")
    public IsSuccessResponseDto deleteReComment(@PathVariable Long reCommentId){
        return commentService.deleteReComment(reCommentId);
    }

    @GetMapping("/recomments/{parentCommentId}")
    public List<ReCommentListDto> getReCommentsAtComments(@PathVariable Long parentCommentId){
        return commentService.getReCommentsAtComments(parentCommentId);
    }
}
