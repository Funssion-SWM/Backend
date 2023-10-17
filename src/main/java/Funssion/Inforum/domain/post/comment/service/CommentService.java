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

        Comment createdComment = commentRepository.createComment(new Comment(
                authorId, authorProfile, LocalDateTime.now(), null, commentSaveDto)
        );
        commentRepository.plusCommentsCountOfPost(commentSaveDto.getPostTypeWithComment(), createdComment.getPostId());
        sendNotificationToPostAuthor(
                commentSaveDto.getPostTypeWithComment(),
                commentSaveDto.getPostId(),
                createdComment);

        return createdComment;
    }

    private void sendNotificationToPostAuthor(PostType receiverPostType, Long receiverPostId, Comment createdComment) {
        Long receiverId = profileRepository.findAuthorId(receiverPostType, receiverPostId);
        if (createdComment.getAuthorId().equals(receiverId)) return;

        notificationRepository.save(
                Notification.builder()
                        .receiverId(receiverId)
                        .receiverPostType(receiverPostType)
                        .receiverPostId(receiverPostId)
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
        Long parentCommentId = reCommentSaveDto.getParentCommentId();

        ReComment createdRecomment = commentRepository.createReComment(new ReComment(
                authorId, authorProfile, LocalDateTime.now(), null, parentCommentId, reCommentSaveDto.getCommentText())
        );

        sendNotification(authorId, parentCommentId, createdRecomment);

        return new IsSuccessResponseDto(true,"대댓글 저장에 성공하였습니다.");
    }

    private void sendNotification(Long authorId, Long parentCommentId, ReComment createdRecomment) {
        Long commentAuthorId = profileRepository.findAuthorId(COMMENT, parentCommentId);
        List<ReCommentListDto> recommentsList = commentRepository.getReCommentsAtComment(parentCommentId, authorId);

        sendNotificationToCommentAuthor(
                commentAuthorId,
                COMMENT,
                parentCommentId,
                createdRecomment);

        for (ReCommentListDto recomment : recommentsList) {
            if (commentAuthorId.equals(recomment.getAuthorId())) continue;
            sendNotificationToCommentAuthor(
                    recomment.getAuthorId(),
                    RECOMMENT,
                    recomment.getId(),
                    createdRecomment
            );
        }
    }

    private void sendNotificationToCommentAuthor(Long receiverId, PostType receiverPostType, Long receiverPostId, ReComment createdReComment) {
        if (!receiverId.equals(createdReComment.getAuthorId())) return;
        notificationRepository.save(
                Notification.builder()
                        .receiverId(receiverId)
                        .receiverPostType(receiverPostType)
                        .receiverPostId(receiverPostId)
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
        notificationRepository.delete(RECOMMENT, reCommentId);
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
