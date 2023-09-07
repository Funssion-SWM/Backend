package Funssion.Inforum.domain.post.comment.controller;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.UnAuthorizedException;
import Funssion.Inforum.domain.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.comment.service.CommentService;
import Funssion.Inforum.domain.post.utils.AuthUtils;
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
        Long authorId = AuthUtils.getUserId(CRUDType.CREATE);
        return commentService.createComment(commentSaveDto,authorId);
    }

    @PatchMapping("/{commentId}")
    public IsSuccessResponseDto updateComment(@RequestBody CommentUpdateDto commentUpdateDto, @PathVariable Long commentId){
        checkAuthorization(CRUDType.UPDATE, commentId,false);
        return commentService.updateComment(commentUpdateDto,commentId);
    }

    @DeleteMapping("/{commentId}")
    public IsSuccessResponseDto deleteComment(@PathVariable Long commentId){
        checkAuthorization(CRUDType.DELETE, commentId,false);
        return commentService.deleteComment(commentId);
    }

    @GetMapping("/{postType}/{postId}")
    public List<CommentListDto> getCommentsAtPostIn(@PathVariable String postType, @PathVariable Long postId){
        Long userId = AuthUtils.getUserId(CRUDType.READ);
        return commentService.getCommentsAtPost(PostType.valueOf(postType.toUpperCase()),postId,userId);
        /* To Do
            converter 사용할 것
         */
    }

    @PostMapping("/recomments")
    public IsSuccessResponseDto createReComment(@RequestBody ReCommentSaveDto reCommentSaveDto){
        Long authorId = AuthUtils.getUserId(CRUDType.CREATE);
        return commentService.createReComment(reCommentSaveDto,authorId);
    }

    @PatchMapping("/recomments/{reCommentId}")
    public IsSuccessResponseDto updateReComment(@RequestBody ReCommentUpdateDto reCommentUpdateDto, @PathVariable Long reCommentId){
        checkAuthorization(CRUDType.UPDATE, reCommentId,true);
        return commentService.updateReComment(reCommentUpdateDto,reCommentId);
    }

    @DeleteMapping("/recomments/{reCommentId}")
    public IsSuccessResponseDto deleteReComment(@PathVariable Long reCommentId){
        checkAuthorization(CRUDType.UPDATE, reCommentId,true);
        return commentService.deleteReComment(reCommentId);
    }

    @GetMapping("/recomments/{parentCommentId}")
    public List<ReCommentListDto> getReCommentsAtComments(@PathVariable Long parentCommentId){
        Long userId = AuthUtils.getUserId(CRUDType.READ);
        return commentService.getReCommentsAtComments(parentCommentId,userId);
    }

    @PostMapping("/like/{commentId}")
    public LikeResponseDto likeComments(@PathVariable Long commentId,@RequestParam(required=true) String isReComment){
        Long userId = AuthUtils.getUserId(CRUDType.CREATE);
        return commentService.likeComments(commentId,Boolean.valueOf(isReComment),userId);
    }
    @DeleteMapping("/like/{commentId}")
    public LikeResponseDto cancelLikeComments(@PathVariable Long commentId,@RequestParam(required=true) String isReComment){
        Long userId = AuthUtils.getUserId(CRUDType.DELETE);
        return commentService.cancelLikeComments(commentId,Boolean.valueOf(isReComment),userId);
    }

    private void checkAuthorization(CRUDType crudType, Long commentId, boolean isReComment) {
        Long userId = AuthUtils.getUserId(crudType);
        if (!userId.equals(commentService.getAuthorIdOfComment(commentId,isReComment))) {
            throw new UnAuthorizedException("Permission denied to "+crudType.toString());
        }
    }
}
