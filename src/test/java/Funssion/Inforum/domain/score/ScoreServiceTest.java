package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.score.ScoreService.updateData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static Funssion.Inforum.domain.score.ScoreService.LIMIT_DAILY_SCORE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTest {
    private final long SCORE_ROUND_LIMIT = LIMIT_DAILY_SCORE - 1L;
    @Mock
    ScoreRepository scoreRepository;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    ScoreService scoreService;
    Long userId = 1L;
    @Nested
    @DisplayName("유저의 Score가 증가하는 경우")
    class addScoreOfUser{
        @Test
        @DisplayName("유저가 일별 최대 점수를 정확히 채웠을 경우")
        void addScoreWhenSameAsDailyLimit(){
            when(scoreRepository.getUserDailyScore(userId)).thenReturn(150L);
            updateData addedScoreAndDailyScore = scoreService.getAddedScoreAndDailyScore(userId, ScoreType.MAKE_MEMO);
            Assertions.assertThat(addedScoreAndDailyScore.addScore()).isEqualTo(50L);
            Assertions.assertThat(addedScoreAndDailyScore.updateDailyScore()).isEqualTo(200L);
        }
        @Test
        @DisplayName("유저가 일별 최대 점수를 채우지 않은 경우")
        void addScoreWhenNotOverDailyLimit(){
            when(scoreRepository.getUserDailyScore(userId)).thenReturn(110L);
            updateData addedScoreAndDailyScore = scoreService.getAddedScoreAndDailyScore(userId, ScoreType.MAKE_MEMO);
            Assertions.assertThat(addedScoreAndDailyScore.addScore()).isEqualTo(50L);
            Assertions.assertThat(addedScoreAndDailyScore.updateDailyScore()).isEqualTo(160L);
        }

        @Test
        @DisplayName("유저가 일별 최대 점수를 이미 채운 경우")
        void addScoreWhenAlreadyDailyLimit(){
            when(scoreRepository.getUserDailyScore(userId)).thenReturn(LIMIT_DAILY_SCORE);
            updateData addedScoreAndDailyScore = scoreService.getAddedScoreAndDailyScore(userId, ScoreType.MAKE_MEMO);
            Assertions.assertThat(addedScoreAndDailyScore.addScore()).isEqualTo(0L);
            Assertions.assertThat(addedScoreAndDailyScore.updateDailyScore()).isEqualTo(LIMIT_DAILY_SCORE);
        }

        @Test
        @DisplayName("유저가 행한 행동이 일별 최대 점수를 초과하는 경우")
        void addScoreWhenOverDailyLimit(){
            when(scoreRepository.getUserDailyScore(userId)).thenReturn(SCORE_ROUND_LIMIT);
            updateData addedScoreAndDailyScore = scoreService.getAddedScoreAndDailyScore(userId, ScoreType.MAKE_MEMO);
            Assertions.assertThat(addedScoreAndDailyScore.addScore()).isEqualTo(LIMIT_DAILY_SCORE-SCORE_ROUND_LIMIT);
            Assertions.assertThat(addedScoreAndDailyScore.updateDailyScore()).isEqualTo(LIMIT_DAILY_SCORE);
        }
    }
    @Nested
    @DisplayName("삭제등의 행동으로 점수를 차감하는 경우")
    class minusScoreOfUser{
        @Test
        @DisplayName("유저가 어떤 행동으로 점수를 얻은 시점이 하루 이전일 때, 그 행동을 되돌리는 경우")
        void minusScoreBeforeDay(){
            Score score = Score.builder()
                    .id(1L)
                    .userId(userId)
                    .createdDate(LocalDateTime.now().minusDays(1))
                    .scoreType(ScoreType.MAKE_MEMO)
                    .score(50L)
                    .postId(1L)
                    .build();
            when(memberRepository.getDailyScore(userId)).thenReturn(LIMIT_DAILY_SCORE);
            when(scoreRepository.findScoreHistoryInfoById(userId,ScoreType.MAKE_MEMO,1L)).thenReturn(Optional.ofNullable(score));

            Assertions.assertThatCode(()->scoreService.subtractUserScore(userId,ScoreType.MAKE_MEMO,1L)).doesNotThrowAnyException();

            verify(scoreRepository, times(1)).updateUserScoreAtOtherDay(userId,ScoreType.MAKE_MEMO.getScore());
        }
        @Test
        @DisplayName("유저가 어떤 행동으로 점수를 얻은 시점이 그 당일일 때, 그 행동을 되돌리는 경우")
        void minusScoreToday(){
            LocalDateTime now = LocalDateTime.now();

            Score score = Score.builder()
                    .id(1L)
                    .userId(userId)
                    .createdDate(LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),now.getHour(),0))
                    .scoreType(ScoreType.MAKE_MEMO)
                    .score(50L)
                    .postId(1L)
                    .build();
            when(memberRepository.getDailyScore(userId)).thenReturn(LIMIT_DAILY_SCORE);
            when(scoreRepository.findScoreHistoryInfoById(userId,score.getScoreType(),score.getPostId())).thenReturn(Optional.ofNullable(score));

            Assertions.assertThatCode(()->scoreService.subtractUserScore(userId,score.getScoreType(),score.getPostId())).doesNotThrowAnyException();

            verify(scoreRepository, times(1)).updateUserScoreAtDay(userId,ScoreType.MAKE_MEMO.getScore(),LIMIT_DAILY_SCORE - score.getScore());
        }
    }

}