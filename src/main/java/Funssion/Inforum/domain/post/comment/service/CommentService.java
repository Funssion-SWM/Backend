package Funssion.Inforum.domain.post.comment.service;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.UnAuthorizedException;
import Funssion.Inforum.domain.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.IsSuccessResponseDto;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

import static java.time.LocalDate.now;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MyRepository myRepository;

    /*
     * 최초 댓글/대댓글 생성의 경우 Profile정보가 필요하므로 Service AuthUtils.getUserId 로
     * 로그인된 유저의 정보를 가져와
     * 이를 활용하여 profile을 가져옵니다.
     */
    public IsSuccessResponseDto createComment(CommentSaveDto commentSaveDto){
        Long authorId = AuthUtils.getUserId(CRUDType.CREATE);
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);
        commentRepository.createComment(new Comment(
            authorId,authorProfile, Date.valueOf(now()),null,commentSaveDto)
        );
        return new IsSuccessResponseDto(true,"댓글 저장에 성공하였습니다.");
    }

    public IsSuccessResponseDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId) {
        checkAuthorization(CRUDType.UPDATE, commentId,false);
        return commentRepository.updateComment(commentUpdateDto,commentId);
    }

    public IsSuccessResponseDto deleteComment(Long commentId) {
        checkAuthorization(CRUDType.DELETE, commentId,false);
        return commentRepository.deleteComment(commentId);
    }

    public List<CommentListDto> getCommentsAtPost(PostType postType, Long postId){
        Long userId = AuthUtils.getUserId(CRUDType.READ);
        return commentRepository.getCommentsAtPost(postType, postId,userId);
    }

    public IsSuccessResponseDto createReComment(ReCommentSaveDto reCommentSaveDto){
        Long authorId = AuthUtils.getUserId(CRUDType.CREATE);
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);

        commentRepository.createReComment(new ReComment(
                authorId,authorProfile, Date.valueOf(now()),null, reCommentSaveDto.getParentCommentId(),reCommentSaveDto.getCommentText())
        );
        return new IsSuccessResponseDto(true,"대댓글 저장에 성공하였습니다.");
    }

    public IsSuccessResponseDto updateReComment(ReCommentUpdateDto reCommentUpdateDto, Long reCommentId) {
        checkAuthorization(CRUDType.UPDATE, reCommentId,true);
        return commentRepository.updateReComment(reCommentUpdateDto,reCommentId);
    }

    public IsSuccessResponseDto deleteReComment(Long reCommentId) {
        checkAuthorization(CRUDType.DELETE, reCommentId,true);
        return commentRepository.deleteReComment(reCommentId);
    }

    public List<ReCommentListDto> getReCommentsAtComments(Long parentCommentId) {
        Long userId = AuthUtils.getUserId(CRUDType.READ);
        return commentRepository.getReCommentsAtComment(parentCommentId,userId);
    }

    public LikeResponseDto likeComments(Long commentId, Boolean isReComment){
        return commentRepository.likeComment(commentId,isReComment);
    }
    public LikeResponseDto cancelLikeComments(Long commentId, Boolean isReComment){
        return commentRepository.cancelLikeComment(commentId,isReComment);
    }

    private void checkAuthorization(CRUDType crudType, Long commentId, boolean isReComment) {
        Long userId = AuthUtils.getUserId(crudType);
        if (!userId.equals(commentRepository.findAuthorIdByCommentId(commentId,isReComment))) {
            throw new UnAuthorizedException("Permission denied to "+crudType.toString());
        }
    }
}
