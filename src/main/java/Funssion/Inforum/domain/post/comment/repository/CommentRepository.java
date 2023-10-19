package Funssion.Inforum.domain.post.comment.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.PostIdAndTypeInfo;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.like.dto.response.LikeResponseDto;

import java.util.List;

public interface CommentRepository {
    Comment createComment(Comment comment);

    ReComment createReComment(ReComment reComment);
    IsSuccessResponseDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId);

    IsSuccessResponseDto updateReComment(ReCommentUpdateDto reCommentUpdateDto, Long reCommentId);

    IsSuccessResponseDto deleteComment(Long commentId);
    IsSuccessResponseDto deleteReComment(Long reCommentId);
    /*
       getComments 관련하여, 댓글 좋아요 여부 정보를 로그인 상태에 따라,
       member.like table과의 join으로 처리합니다. -> 튜닝해야할 지도
     */

    List<CommentListDto> getCommentsAtPost(PostType postType, Long postId, Long userId);

    List<ReCommentListDto> getReCommentsAtComment(Long parentCommentId, Long userId);

    LikeResponseDto likeComment(Long commentId, Boolean isReComment,Long userId);

    LikeResponseDto cancelLikeComment(Long commentId, Boolean isReComment,Long userId);

    Long findAuthorIdByCommentId(Long commentId, Boolean isReComment);

    void updateProfileImageOfComment(Long userId, String authorProfileImagePath);

    void updateProfileImageOfReComment(Long userId, String authorProfileImagePath);

    PostIdAndTypeInfo getPostIdByCommentId(Long commentId);

    void plusCommentsCountOfPost(PostType postType, Long postId);
    void subtractCommentsCountOfPost(PostIdAndTypeInfo postIdAndTypeInfo);

    List<Comment> findIfUserRegisterAnotherCommentOfPost(Long userId, Long postId);

}
