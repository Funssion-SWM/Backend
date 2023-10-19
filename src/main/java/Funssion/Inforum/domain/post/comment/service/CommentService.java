package Funssion.Inforum.domain.post.comment.service;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
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
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.profile.ProfileRepository;
import Funssion.Inforum.domain.score.repository.ScoreRepository;
import Funssion.Inforum.domain.score.service.ScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static Funssion.Inforum.common.constant.NotificationType.NEW_COMMENT;
import static Funssion.Inforum.common.constant.PostType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final ScoreService scoreService;

    private final CommentRepository commentRepository;
    private final MyRepository myRepository;
    private final ScoreRepository scoreRepository;
    private final ProfileRepository profileRepository;
    private final NotificationRepository notificationRepository;
    private final AnswerRepository answerRepository;

    /*
     * 최초 댓글/대댓글 생성의 경우 Profile정보가 필요하므로 Service AuthUtils.getUserId 로
     * 로그인된 유저의 정보를 가져와
     * 이를 활용하여 profile을 가져옵니다.
     */

    /**
     * Comment의 경우 최초 등록한 댓글에만 점수를 반영합니다.
     * 또한 두개의 댓글이 존재하고 한개가 삭제되면, 남은 댓글이 있으므로 이를 통해 점수를 재 반영합니다.
     */
    @Transactional
    public Comment createComment(CommentSaveDto commentSaveDto, Long authorId){
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);

        Comment createdComment = commentRepository.createComment(new Comment(
                authorId, authorProfile, LocalDateTime.now(), null, commentSaveDto)
        );
        if(commentRepository.findIfUserRegisterAnotherCommentOfPost(authorId, createdComment.getId()).isEmpty())
            scoreService.checkUserDailyScoreAndAdd(authorId,ScoreType.MAKE_COMMENT,createdComment.getId());
        commentRepository.plusCommentsCountOfPost(commentSaveDto.getPostTypeWithComment(), createdComment.getPostId());
        sendNotificationToPostAuthor(
                commentSaveDto.getPostTypeWithComment(),
                commentSaveDto.getPostId(),
                createdComment);

        return createdComment;
    }

    private void sendNotificationToPostAuthor(PostType postTypeWithComment, Long postIdWithComment, Comment createdComment) {
        Long receiverId = profileRepository.findAuthorId(postTypeWithComment, postIdWithComment);
        if (createdComment.getAuthorId().equals(receiverId)) return;

        PostIdAndTypeInfo postInfoToShow = getPostInfoToShowInComment(postTypeWithComment, postIdWithComment);

        notificationRepository.save(
                Notification.builder()
                        .receiverId(receiverId)
                        .postTypeToShow(postInfoToShow.getPostType())
                        .postIdToShow(postInfoToShow.getPostId())
                        .senderId(createdComment.getAuthorId())
                        .senderName(createdComment.getAuthorName())
                        .senderImagePath(createdComment.getAuthorImagePath())
                        .senderRank(createdComment.getRank())
                        .senderPostType(COMMENT)
                        .senderPostId(createdComment.getId())
                        .notificationType(NEW_COMMENT)
                        .build()
        );
    }

    private PostIdAndTypeInfo getPostInfoToShowInComment(PostType postTypeWithComment, Long postIdWithComment) {
        switch (postTypeWithComment) {
            case MEMO, QUESTION -> {
                return new PostIdAndTypeInfo(postIdWithComment, postTypeWithComment);
            }
            case ANSWER -> {
                Answer commentedAnswer = answerRepository.getAnswerById(postIdWithComment);
                return new PostIdAndTypeInfo(commentedAnswer.getQuestionId(), QUESTION);
            }
            default -> throw new BadRequestException("댓글을 달 수 없는 게시물입니다.");
        }
    }

    @Transactional
    public IsSuccessResponseDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId) {
        return commentRepository.updateComment(commentUpdateDto,commentId);
    }

    @Transactional
    public IsSuccessResponseDto deleteComment(Long commentId) {
        PostIdAndTypeInfo postIdByCommentId = commentRepository.getPostIdByCommentId(commentId);
        commentRepository.subtractCommentsCountOfPost(postIdByCommentId);
        scoreService.subtractUserScore(commentRepository.findAuthorIdByCommentId(commentId,false),ScoreType.MAKE_COMMENT,commentId);
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

        return new IsSuccessResponseDto(true,"대댓글이 등록되었습니다.");
    }

    private void sendNotification(Long authorId, Long parentCommentId, ReComment createdRecomment) {
        Long commentAuthorId = profileRepository.findAuthorId(COMMENT, parentCommentId);
        List<ReCommentListDto> recommentsList = commentRepository.getReCommentsAtComment(parentCommentId, authorId);
        ArrayList<Long> noticedUserIdList = new ArrayList<>();
        noticedUserIdList.add(authorId);

        sendNotificationToCommentAuthor(
                commentAuthorId,
                COMMENT,
                parentCommentId,
                createdRecomment,
                noticedUserIdList);

        for (ReCommentListDto recomment : recommentsList) {
            sendNotificationToCommentAuthor(
                    recomment.getAuthorId(),
                    RECOMMENT,
                    parentCommentId,
                    createdRecomment,
                    noticedUserIdList
            );
        }
    }

    private void sendNotificationToCommentAuthor(Long receiverId, PostType postTypeWithRecomment, Long postIdWithRecomment, ReComment createdReComment, ArrayList<Long> noticedUserIdList) {
        if (noticedUserIdList.contains(receiverId)) return;
        PostIdAndTypeInfo postInfoToShowInRecomment = getPostInfoToShowInRecomment(postTypeWithRecomment, postIdWithRecomment);

        notificationRepository.save(
                Notification.builder()
                        .receiverId(receiverId)
                        .postTypeToShow(postInfoToShowInRecomment.getPostType())
                        .postIdToShow(postInfoToShowInRecomment.getPostId())
                        .senderId(createdReComment.getAuthorId())
                        .senderName(createdReComment.getAuthorName())
                        .senderImagePath(createdReComment.getAuthorImagePath())
                        .senderRank(createdReComment.getRank())
                        .senderPostType(RECOMMENT)
                        .senderPostId(createdReComment.getId())
                        .notificationType(NEW_COMMENT)
                        .build()
        );

        noticedUserIdList.add(receiverId);
    }

    private PostIdAndTypeInfo getPostInfoToShowInRecomment(PostType postTypeWithRecomment, Long postIdWithRecomment) {
        switch (postTypeWithRecomment) {
            case COMMENT, RECOMMENT -> {
                PostIdAndTypeInfo postIdAndTypeInfoToShowInComment = commentRepository.getPostIdByCommentId(postIdWithRecomment);
                return getPostInfoToShowInComment(
                        postIdAndTypeInfoToShowInComment.getPostType(),
                        postIdAndTypeInfoToShowInComment.getPostId());
            }
            default -> throw new BadRequestException("대댓글을 달 수 없는 게시물입니다.");
        }
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
