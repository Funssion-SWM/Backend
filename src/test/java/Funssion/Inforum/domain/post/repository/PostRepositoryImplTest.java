package Funssion.Inforum.domain.post.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.domain.mypage.domain.ScoreAndCountDao;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.score.Rank;
import Funssion.Inforum.domain.score.repository.ScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    ScoreRepository scoreRepository;
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
                        .rank(Rank.BRONZE_5.toString())
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
                        .rank(Rank.BRONZE_5.toString())
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
                        .rank(Rank.BRONZE_5.toString())
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
    @Test
    @DisplayName("포스트 타입별 점수와 점수를 얻은 횟수 가져오기")
    void getScoreAndCountOfEachScoreType() {
        Long userId = 10L;
        Long likedUserId = 20L;
        scoreRepository.saveScoreHistory(userId, ScoreType.LIKE, ScoreType.LIKE.getScore(), 1L,PostType.SERIES,likedUserId);
        scoreRepository.saveScoreHistory(likedUserId, ScoreType.MAKE_MEMO, 35L, 1L);
        scoreRepository.saveScoreHistory(likedUserId, ScoreType.MAKE_MEMO, ScoreType.MAKE_MEMO.getScore(), 2L);
        ScoreAndCountDao allPostScoreAndCount = postRepository.getAllPostScoreAndCount(likedUserId);
        assertThat(allPostScoreAndCount.getLikeScoreAndCount().getCount()).isEqualTo(1L);
        assertThat(allPostScoreAndCount.getLikeScoreAndCount().getScore()).isEqualTo(ScoreType.LIKE.getScore());
        assertThat(allPostScoreAndCount.getMemoScoreAndCount().getScore()).isEqualTo(35L+50L);
        assertThat(allPostScoreAndCount.getMemoScoreAndCount().getCount()).isEqualTo(2L);


    }
}