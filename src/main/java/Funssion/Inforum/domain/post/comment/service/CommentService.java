package Funssion.Inforum.domain.post.comment.service;

import Funssion.Inforum.common.constant.NotificationType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.domain.ReComment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.ReCommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.PostIdAndTypeInfo;
import Funssion.Inforum.domain.post.comment.dto.response.ReCommentListDto;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.profile.ProfileRepository;
import Funssion.Inforum.domain.profile.domain.AuthorProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static Funssion.Inforum.common.constant.NotificationType.*;
import static Funssion.Inforum.common.constant.PostType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MyRepository myRepository;
    private final ProfileRepository profileRepository;
    private final NotificationRepository notificationRepository;

    /*
     * 최초 댓글/대댓글 생성의 경우 Profile정보가 필요하므로 Service AuthUtils.getUserId 로
     * 로그인된 유저의 정보를 가져와
     * 이를 활용하여 profile을 가져옵니다.
     */
    @Transactional
    public Comment createComment(CommentSaveDto commentSaveDto, Long authorId){
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);

        Comment comment = commentRepository.createComment(new Comment(
                authorId, authorProfile, LocalDateTime.now(), null, commentSaveDto)
        );
        commentRepository.plusCommentsCountOfPost(commentSaveDto.getPostTypeWithComment(), comment.getPostId());
        addNotificationToPostAuthor(commentSaveDto, comment);

        return comment;
    }

    private void addNotificationToPostAuthor(CommentSaveDto commentSaveDto, Comment createdComment) {
        PostType postTypeWithComment = commentSaveDto.getPostTypeWithComment();
        Long postId = commentSaveDto.getPostId();
        AuthorProfile authorProfile = profileRepository.findAuthorProfile(postTypeWithComment, postId);
        notificationRepository.save(
                Notification.builder()
                        .receiverId(authorProfile.getId())
                        .receiverPostType(postTypeWithComment)
                        .receiverPostId(postId)
                        .senderId(createdComment.getAuthorId())
                        .senderName(createdComment.getAuthorName())
                        .senderImagePath(createdComment.getAuthorImagePath())
                        .senderPostType(COMMENT)
                        .senderPostId(createdComment.getId())
                        .notificationType(NEW_COMMENT)
                        .build()
        );
    }

    @Transactional
    public IsSuccessResponseDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId) {
        return commentRepository.updateComment(commentUpdateDto,commentId);
    }

    @Transactional
    public IsSuccessResponseDto deleteComment(Long commentId) {
        PostIdAndTypeInfo postIdByCommentId = commentRepository.getPostIdByCommentId(commentId);
        commentRepository.subtractCommentsCountOfPost(postIdByCommentId);
        notificationRepository.delete(COMMENT, commentId);
        return commentRepository.deleteComment(commentId);
    }

    @Transactional(readOnly = true)
    public List<CommentListDto> getCommentsAtPost(PostType postType, Long postId,Long userId){
        return commentRepository.getCommentsAtPost(postType, postId,userId);
    }

    @Transactional
    public IsSuccessResponseDto createReComment(ReCommentSaveDto reCommentSaveDto,Long authorId){
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);

        ReComment createdRecomment = commentRepository.createReComment(new ReComment(
                authorId, authorProfile, LocalDateTime.now(), null, reCommentSaveDto.getParentCommentId(), reCommentSaveDto.getCommentText())
        );
        addNotificationToPostAuthor(reCommentSaveDto, createdRecomment);
        return new IsSuccessResponseDto(true,"대댓글 저장에 성공하였습니다.");
    }

    private void addNotificationToPostAuthor(ReCommentSaveDto reCommentSaveDto, ReComment createdReComment) {
        Long parentCommentId = reCommentSaveDto.getParentCommentId();
        AuthorProfile authorProfile = profileRepository.findAuthorProfile(COMMENT, parentCommentId);
        notificationRepository.save(
                Notification.builder()
                        .receiverId(authorProfile.getId())
                        .receiverPostType(COMMENT)
                        .receiverPostId(parentCommentId)
                        .senderId(createdReComment.getAuthorId())
                        .senderName(createdReComment.getAuthorName())
                        .senderImagePath(createdReComment.getAuthorImagePath())
                        .senderPostType(RECOMMENT)
                        .senderPostId(createdReComment.getId())
                        .notificationType(NEW_COMMENT)
                        .build()
        );
    }

    public IsSuccessResponseDto updateReComment(ReCommentUpdateDto reCommentUpdateDto, Long reCommentId) {
        return commentRepository.updateReComment(reCommentUpdateDto,reCommentId);
    }

    public IsSuccessResponseDto deleteReComment(Long reCommentId) {
        return commentRepository.deleteReComment(reCommentId);
    }

    public List<ReCommentListDto> getReCommentsAtComments(Long parentCommentId,Long userId) {
        return commentRepository.getReCommentsAtComment(parentCommentId,userId);
    }

    public LikeResponseDto likeComments(Long commentId, Boolean isReComment,Long userId){
        return commentRepository.likeComment(commentId,isReComment,userId);
    }
    public LikeResponseDto cancelLikeComments(Long commentId, Boolean isReComment,Long userId){
        return commentRepository.cancelLikeComment(commentId,isReComment,userId);
    }

    public Long getAuthorIdOfComment(Long commentId, Boolean isReComment){
        return commentRepository.findAuthorIdByCommentId(commentId,isReComment);
    }

}
