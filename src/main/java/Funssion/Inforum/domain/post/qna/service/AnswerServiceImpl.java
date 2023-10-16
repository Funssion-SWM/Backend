package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.common.constant.NotificationType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import Funssion.Inforum.domain.profile.ProfileRepository;
import Funssion.Inforum.domain.profile.domain.AuthorProfile;
import Funssion.Inforum.s3.S3Repository;
import Funssion.Inforum.s3.S3Utils;
import Funssion.Inforum.s3.dto.response.ImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static Funssion.Inforum.common.constant.CRUDType.UPDATE;
import static Funssion.Inforum.common.constant.NotificationType.*;
import static Funssion.Inforum.common.constant.PostType.ANSWER;
import static Funssion.Inforum.common.constant.PostType.QUESTION;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final MyRepository myRepository;
    private final S3Repository s3Repository;
    private final QuestionRepository questionRepository;
    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;

    @Value("${aws.s3.answer-dir}")
    private String ANSWER_DIR;

    @Override
    @Transactional
    public Answer createAnswerOfQuestion(AnswerSaveDto answerSaveDto, Long questionId, Long authorId) {
        if(isAuthorOfQuestionCreateAnswer(questionId, authorId)){
            throw new BadRequestException("자신이 작성한 질문 글에 답변을 달 수 없습니다.");
        }
        Answer createdAnswer = answerRepository.createAnswer(addAuthorInfo(answerSaveDto, authorId, questionId));
        answerRepository.updateAnswersCountOfQuestion(questionId,Sign.PLUS);
        createOrUpdateHistory(authorId,createdAnswer.getCreatedDate(), Sign.PLUS);
        sendNotificationToQuestionAuthor(questionId, createdAnswer);
        return createdAnswer;
    }

    private void sendNotificationToQuestionAuthor(Long questionId, Answer createdAnswer) {
        notificationRepository.save(
                Notification.builder()
                        .receiverId(profileRepository.findAuthorId(QUESTION, questionId))
                        .receiverPostType(QUESTION)
                        .receiverPostId(questionId)
                        .senderId(createdAnswer.getAuthorId())
                        .senderPostType(ANSWER)
                        .senderPostId(createdAnswer.getId())
                        .senderName(createdAnswer.getAuthorName())
                        .senderImagePath(createdAnswer.getAuthorImagePath())
                        .notificationType(NEW_ANSWER)
                        .build()
        );
    }

    private boolean isAuthorOfQuestionCreateAnswer(Long questionId, Long authorId) {
        return questionRepository.getOneQuestion(questionId).getAuthorId().equals(authorId);
    }

    @Override
    public List<Answer> getAnswersOfQuestion(Long loginId, Long questionId) {
        return answerRepository.getAnswersOfQuestion(loginId, questionId);
    }

    @Override
    public Long getAuthorId(Long answerId) {
        return answerRepository.getAuthorIdOf(answerId);
    }

    @Override
    public Answer updateAnswer(AnswerSaveDto answerSaveDto, Long answerId) {
        return answerRepository.updateAnswer(answerSaveDto, answerId);
    }

    @Override
    public Answer getAnswerBy(Long answerId) {
        return answerRepository.getAnswerById(answerId);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long answerId, Long authorId) {
        Answer willBeDeletedAnswer = answerRepository.getAnswerById(answerId);
        answerRepository.updateAnswersCountOfQuestion(willBeDeletedAnswer.getQuestionId(),Sign.MINUS);
        s3Repository.deleteFromText(ANSWER_DIR, willBeDeletedAnswer.getText());
        createOrUpdateHistory(authorId,willBeDeletedAnswer.getCreatedDate(), Sign.MINUS);
        notificationRepository.delete(ANSWER, answerId);
        answerRepository.deleteAnswer(answerId);
    }

    @Override
    public ImageDto saveImageAndGetImageURL(MultipartFile image) {
        Long userId = AuthUtils.getUserId(UPDATE);
        String imageName = S3Utils.generateImageNameOfS3(userId);

        String uploadedURL = s3Repository.upload(image, ANSWER_DIR, imageName);

        return ImageDto.builder()
                .imageName(imageName)
                .imagePath(uploadedURL)
                .build();
    }

    @Override
    public Answer selectAnswer(Long loginId, Long questionId, Long answerId) {
        if(isNotUserAuthorOfQuestion(loginId, questionId)) throw new UnAuthorizedException("답변을 채택할 권한이 없습니다.");
        questionRepository.solveQuestion(questionId);
        sendNotificationToSelectedAnswerAuthor(answerId, loginId, questionId);
        return answerRepository.select(answerId);
    }

    private void sendNotificationToSelectedAnswerAuthor(Long receiverPostId, Long senderId, Long senderPostId) {
        AuthorProfile senderProfile = profileRepository.findAuthorProfile(QUESTION, senderPostId);
        notificationRepository.save(
                Notification.builder()
                        .receiverId(profileRepository.findAuthorId(ANSWER, receiverPostId))
                        .receiverPostType(ANSWER)
                        .receiverPostId(receiverPostId)
                        .senderId(senderId)
                        .senderPostType(QUESTION)
                        .senderPostId(senderPostId)
                        .senderName(senderProfile.getName())
                        .senderImagePath(senderProfile.getProfileImagePath())
                        .notificationType(NEW_ACCEPTED)
                        .build()
        );
    }

    private boolean isNotUserAuthorOfQuestion(Long loginId, Long questionId) {
        return !loginId.equals(questionRepository.getAuthorId(questionId));
    }

    private Answer addAuthorInfo(AnswerSaveDto answerSaveDto, Long authorId, Long questionId) {
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);
        Answer answerSaveDtoWithAuthor = addAuthor(answerSaveDto, authorId, authorProfile,questionId);
        return answerSaveDtoWithAuthor;
    }
    private Answer addAuthor(AnswerSaveDto answerSaveDto, Long authorId, MemberProfileEntity authorProfile, Long questionId) {
        Long defaultRepliesCount = 0L;
        Long defaultDislikes = 0L;
        boolean isSelected = false;
        return new Answer(authorId,authorProfile, LocalDateTime.now(),null, answerSaveDto.getText(),questionId, defaultDislikes, isSelected,defaultRepliesCount);
    }

    private void createOrUpdateHistory(Long userId, LocalDateTime curDate, Sign sign) {
        try {
            myRepository.updateHistory(userId, ANSWER, sign, curDate.toLocalDate());
        } catch (HistoryNotFoundException e) {
            myRepository.createHistory(userId, ANSWER);
        }
    }
}
