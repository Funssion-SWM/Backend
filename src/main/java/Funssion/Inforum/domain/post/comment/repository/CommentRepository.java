package Funssion.Inforum.domain.post.comment.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.IsSuccessResponseDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;

import java.util.List;

public interface CommentRepository {
    void createComment(Comment comment);

    void createReComment(ReComment reComment);
    IsSuccessResponseDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId);

    IsSuccessResponseDto updateReComment(ReCommentUpdateDto reCommentUpdateDto, Long reCommentId);

    IsSuccessResponseDto deleteComment(Long commentId);
    /*
       getComments 관련하여, 댓글 좋아요 여부 정보를 로그인 상태에 따라,
       member.like table과의 join으로 처리합니다. -> 튜닝해야할 지도
     */
    List<CommentListDto> getCommentsAtPost(PostType postType, Long postId);

    List<ReCommentListDto> getReCommentsAtComment(Long parentCommentId);

    LikeResponseDto likeComment(PostType postType, Long commentId);

}
