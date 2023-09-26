package Funssion.Inforum.domain.post.qna.service;

import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.dto.response.UploadedQuestionDto;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final MyRepository myRepository;
    @Override
    public UploadedQuestionDto createQuestion(QuestionSaveDto questionSaveDto, Long authorId)
    {
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);
        Question questionSaveDtoWithAuthor = addAuthor(questionSaveDto, authorId, authorProfile);
        return questionRepository.createQuestion(questionSaveDtoWithAuthor);
    }

    private Question addAuthor(QuestionSaveDto questionSaveDto, Long authorId, MemberProfileEntity authorProfile) {
        return new Question(authorId,authorProfile,LocalDateTime.now(),null, questionSaveDto.getTitle(), questionSaveDto.getText(), questionSaveDto.getTags());
    }
}
