package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.follow.repository.FollowRepository;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;

import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.Constant;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.dto.response.QuestionDto;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import Funssion.Inforum.domain.score.service.ScoreService;
import Funssion.Inforum.domain.profile.ProfileRepository;
import Funssion.Inforum.s3.S3Repository;
import Funssion.Inforum.s3.S3Utils;
import Funssion.Inforum.s3.dto.response.ImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static Funssion.Inforum.common.constant.NotificationType.*;
import static Funssion.Inforum.common.constant.PostType.*;
import static Funssion.Inforum.common.utils.CustomStringUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final ScoreService scoreService;

    private final QuestionRepository questionRepository;
    private final MyRepository myRepository;
    private final MemoRepository memoRepository;
    private final S3Repository s3Repository;
    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;
    private final FollowRepository followRepository;

    @Value("${aws.s3.question-dir}")
    private String QUESTION_DIR;

    @Override
    public Long getAuthorId(Long questionId) {
        return questionRepository.getAuthorId(questionId);
    }

    @Override
    @Transactional
    public Question createQuestion(QuestionSaveDto questionSaveDto, Long authorId, Long memoId)
    {
        findMemoAndUpdateQuestionsCount(memoId, Sign.PLUS);
        Question createdQuestion = questionRepository.createQuestion(addAuthorInfo(questionSaveDto, authorId,memoId));
        createOrUpdateHistory(authorId,createdQuestion.getCreatedDate(),Sign.PLUS);
        scoreService.checkUserDailyScoreAndAdd(authorId,ScoreType.MAKE_QUESTION,createdQuestion.getId());
        sendNotification(memoId, createdQuestion);
        return createdQuestion;
    }

    private void sendNotification(Long memoId, Question createdQuestion) {
        if (memoId.toString().equals(Constant.NONE_MEMO_QUESTION)) return;
        Long receiverId = profileRepository.findAuthorId(MEMO, memoId);
        sendNotificationToLinkedMemoAuthor(receiverId, memoId, createdQuestion);
        sendNotificationToFollower(receiverId, createdQuestion);
    }

    private void sendNotificationToLinkedMemoAuthor(Long receiverId, Long receiverPostId, Question createdQuestion) {
        if (receiverId.equals(createdQuestion.getAuthorId())) return;
        notificationRepository.save(
                Notification.builder()
                        .receiverId(receiverId)
                        .receiverPostType(MEMO)
                        .receiverPostId(receiverPostId)
                        .senderId(createdQuestion.getAuthorId())
                        .senderPostType(QUESTION)
                        .senderRank(createdQuestion.getRank())
                        .senderPostId(createdQuestion.getId())
                        .senderName(createdQuestion.getAuthorName())
                        .senderImagePath(createdQuestion.getAuthorImagePath())
                        .notificationType(NEW_QUESTION)
                        .build()
        );
    }

    private void sendNotificationToFollower(Long receivedUserId, Question createdQuestion) {
        List<Long> followerIdList =
                followRepository.findFollowedUserIdByUserId(createdQuestion.getAuthorId());

        for (Long receiverId : followerIdList) {
            if (receiverId.equals(receivedUserId) || receiverId.equals(createdQuestion.getAuthorId())) continue;
            notificationRepository.save(
                    Notification.builder()
                            .receiverId(receiverId)
                            .senderId(createdQuestion.getAuthorId())
                            .senderPostType(QUESTION)
                            .senderPostId(createdQuestion.getId())
                            .senderName(createdQuestion.getAuthorName())
                            .senderImagePath(createdQuestion.getAuthorImagePath())
                            .notificationType(NEW_POST_FOLLOWED)
                            .build()
            );
        }
    }

    private void findMemoAndUpdateQuestionsCount(Long memoId,Sign sign) {
        if(memoId != Long.valueOf(Constant.NONE_MEMO_QUESTION)){
            memoRepository.updateQuestionsCountOfMemo(memoId,sign);
        }
    }

    private Question addAuthorInfo(QuestionSaveDto questionSaveDto, Long authorId, Long memoId) {
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);
        Question questionSaveDtoWithAuthor = addAuthor(questionSaveDto, authorId, authorProfile,memoId);
        return questionSaveDtoWithAuthor;
    }

    private void createOrUpdateHistory(Long userId, LocalDateTime curDate, Sign sign) {
        try {
            myRepository.updateHistory(userId, QUESTION, sign, curDate.toLocalDate());
        } catch (HistoryNotFoundException e) {
            myRepository.createHistory(userId, QUESTION);
        }
    }
    @Override
    @Transactional
    public Question updateQuestion(QuestionSaveDto questionSaveDto, Long questionId, Long authorId) {
        questionRepository.getOneQuestion(questionId);
        Question question = questionRepository.updateQuestion(questionSaveDto, questionId);
        return question;
    }

    @Override
    public List<Question> getQuestions(Long userId, OrderType orderBy) {
        return questionRepository.getQuestions(userId, orderBy);
    }
    @Override
    public List<Question> getQuestionsOfMemo(Long userId, Long memoId) {
        return questionRepository.getQuestionsOfMemo(userId, memoId);
    }

    @Override
    public List<Question> searchQuestionsBy(
            String searchString,
            Long userId,
            OrderType orderBy,
            Boolean isTag) {

        if (isTag)
            return getQuestionsSearchedByTag(searchString, userId, orderBy);

        return questionRepository.findAllBySearchQuery(getSearchStringList(searchString), orderBy);

    }

    private List<Question> getQuestionsSearchedByTag(String searchString, Long userId, OrderType orderBy) {
        if (userId.equals(SecurityContextUtils.ANONYMOUS_USER_ID))
            return questionRepository.findAllByTag(searchString, orderBy);

        return questionRepository.findAllByTag(searchString, userId, orderBy);
    }

    @Override
    public QuestionDto getOneQuestion(Long loginId, Long questionId) {
        Question question = questionRepository.getOneQuestion(questionId);
        return new QuestionDto(question, loginId);
    }

    @Override
    @Transactional
    public IsSuccessResponseDto deleteQuestion(Long questionId, Long authorId) {
        Question willBeDeletedQuestion = questionRepository.getOneQuestion(questionId);
        findMemoAndUpdateQuestionsCount(willBeDeletedQuestion.getMemoId(), Sign.MINUS);
        if(willBeDeletedQuestion.getAnswersCount() != 0){
            throw new BadRequestException("답변이 달린 질문은 삭제할 수 없습니다.");
        }

        s3Repository.deleteFromText(QUESTION_DIR, willBeDeletedQuestion.getText());
        questionRepository.deleteQuestion(questionId);
        myRepository.updateHistory(authorId,QUESTION,Sign.MINUS,willBeDeletedQuestion.getCreatedDate().toLocalDate());

        scoreService.subtractUserScore(authorId,ScoreType.MAKE_QUESTION, willBeDeletedQuestion.getId());
        notificationRepository.delete(QUESTION, questionId);
        return new IsSuccessResponseDto(true,"성공적으로 질문이 삭제되었습니다.");
    }


    private Question addAuthor(QuestionSaveDto questionSaveDto, Long authorId, MemberProfileEntity authorProfile, Long memoId) {
        Long defaultAnswersCount = 0L;
        Long defaultRepliesCount = 0L;
        boolean isSolved = false;
        return new Question(authorId,authorProfile,LocalDateTime.now(),null, questionSaveDto.getTitle(), questionSaveDto.getDescription(),questionSaveDto.getText(), questionSaveDto.getTags(),defaultRepliesCount, defaultAnswersCount,isSolved,false,memoId);
    }

    @Override
    public ImageDto saveImageAndGetImageURL(MultipartFile image) {
        Long authorId = AuthUtils.getUserId(CRUDType.UPDATE);
        String imageName = S3Utils.generateImageNameOfS3(authorId);

        String uploadedURL = s3Repository.upload(image, QUESTION_DIR, imageName);

        return ImageDto.builder()
                .imagePath(uploadedURL)
                .imageName(imageName)
                .build();
    }
}
