package Funssion.Inforum.domain.post.qna.repository;

import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.score.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuestionRepositoryTest {
    @Autowired
    QuestionRepository questionRepository;

    Question memoQuestion;
    Long targetMemoId = 1L;
    Long memoIdWhichDoNotHaveQuestion = 2L;
    @BeforeEach
    void init(){
        memoQuestion = questionRepository.createQuestion(Question.builder()
                .authorId(1L)
                .authorName("test")
                .authorImagePath("test")
                .title("java")
                .text("{\"content\":\"java is good?\"}")
                .description("java is ...")
                .tags(Collections.emptyList())
                .memoId(targetMemoId)
                .rank(Rank.BRONZE_5.toString())
                .build());
    }

    @Test
    @DisplayName("메모와 연관된 질문인지 아닌지 확인합니다")
    void isMemoQuestionOrNot(){
        assertThat(questionRepository.getQuestionIdByMemoId(targetMemoId).isPresent()).isEqualTo(true);
        assertThat(questionRepository.getQuestionIdByMemoId(memoIdWhichDoNotHaveQuestion).isEmpty()).isEqualTo(true);

    }
}
