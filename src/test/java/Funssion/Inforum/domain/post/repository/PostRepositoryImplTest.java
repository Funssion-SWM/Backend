package Funssion.Inforum.domain.post.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class PostRepositoryImplTest {
    
    @Autowired
    MemoRepository memoRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    PostRepository postRepository;
    
    
    Long testAuthorId = 1L;
    Memo testMemo;
    Question testQuestion;
    Answer testAnswer;
    
    @BeforeEach
    void init() {
        testMemo = memoRepository.create(
                Memo.builder()
                        .authorId(testAuthorId)
                        .authorName("jinu")
                        .authorImagePath("https://image")
                        .title("test")
                        .description("test is ..")
                        .text("{\"content\": \"test is good\"}")
                        .color("yellow")
                        .memoTags(List.of("Test"))
                        .isTemporary(false)
                        .build()
        );
        
        testQuestion = questionRepository.createQuestion(
                Question.builder()
                        .authorId(testAuthorId)
                        .authorName("jinu")
                        .authorImagePath("https://image")
                        .title("test")
                        .description("test is ..")
                        .text("{\"content\": \"test is good?\"}")
                        .tags(List.of("tags"))
                        .memoId(testMemo.getId())
                        .build()
        );

        testAnswer = answerRepository.createAnswer(
                Answer.builder()
                        .questionId(testQuestion.getId())
                        .authorId(testAuthorId)
                        .authorName("jinu")
                        .authorImagePath("https://image")
                        .text("{\"content\": \"test is good.\"}")
                        .build()
        );
    }

    @Test
    @DisplayName("작성자 id 조회하기")
    void findAuthorId() {
        Long memoAuthorId = postRepository.findAuthorId(PostType.MEMO, testMemo.getId());
        Long questionAuthorId = postRepository.findAuthorId(PostType.QUESTION, testQuestion.getId());
        Long answerAuthorId = postRepository.findAuthorId(PostType.ANSWER, testAnswer.getId());

        assertThat(testAuthorId)
                .isEqualTo(memoAuthorId)
                .isEqualTo(questionAuthorId)
                .isEqualTo(answerAuthorId);
    }
}