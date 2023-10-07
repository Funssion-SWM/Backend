package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.Constant;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.dto.response.QuestionDto;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
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

import static Funssion.Inforum.common.constant.PostType.QUESTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final MyRepository myRepository;
    private final MemoRepository memoRepository;
    private final S3Repository s3Repository;

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
        Question question = questionRepository.createQuestion(addAuthorInfo(questionSaveDto, authorId,memoId));
        createOrUpdateHistory(authorId,question.getCreatedDate(),Sign.PLUS);
        return question;
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
            return new IsSuccessResponseDto(false,"답변이 등록된 질문은 삭제할 수 없습니다.");
        }

        s3Repository.deleteFromText(QUESTION_DIR, willBeDeletedQuestion.getText());
        questionRepository.deleteQuestion(questionId);
        myRepository.updateHistory(authorId,QUESTION,Sign.MINUS,willBeDeletedQuestion.getCreatedDate().toLocalDate());
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
