package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
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
import static Funssion.Inforum.common.constant.PostType.ANSWER;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final MyRepository myRepository;
    private final S3Repository s3Repository;

    @Value("${aws.s3.answer-dir}")
    private static String ANSWER_DIR;

    @Override
    @Transactional
    public Answer createAnswerOfQuestion(AnswerSaveDto answerSaveDto, Long questionId, Long authorId) {
        Answer answer = answerRepository.createAnswer(addAuthorInfo(answerSaveDto, authorId, questionId));
        createOrUpdateHistory(authorId,answer.getCreatedDate(), Sign.PLUS);
        return answer;
    }

    @Override
    public List<Answer> getAnswersOfQuestion(Long questionId) {
        return answerRepository.getAnswersOfQuestion(questionId);
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
        s3Repository.deleteFromText(ANSWER_DIR, willBeDeletedAnswer.getText());
        createOrUpdateHistory(authorId,willBeDeletedAnswer.getCreatedDate(), Sign.MINUS);
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

    private Answer addAuthorInfo(AnswerSaveDto answerSaveDto, Long authorId, Long questionId) {
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);
        Answer answerSaveDtoWithAuthor = addAuthor(answerSaveDto, authorId, authorProfile,questionId);
        return answerSaveDtoWithAuthor;
    }
    private Answer addAuthor(AnswerSaveDto answerSaveDto, Long authorId, MemberProfileEntity authorProfile, Long questionId) {
        Long defaultRepliesCount = 0L;
        boolean isSelected = false;
        return new Answer(authorId,authorProfile, LocalDateTime.now(),null, answerSaveDto.getText(),questionId,isSelected,defaultRepliesCount);
    }

    private void createOrUpdateHistory(Long userId, LocalDateTime curDate, Sign sign) {
        try {
            myRepository.updateHistory(userId, ANSWER, sign, curDate.toLocalDate());
        } catch (HistoryNotFoundException e) {
            myRepository.createHistory(userId, ANSWER);
        }
    }
}
