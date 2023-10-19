package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.post.repository.PostRepository;
import Funssion.Inforum.domain.score.domain.Score;
import Funssion.Inforum.domain.score.repository.ScoreRepository;
import Funssion.Inforum.domain.score.service.ScoreService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static Funssion.Inforum.domain.score.Rank.BRONZE_1;
import static Funssion.Inforum.domain.score.Rank.SILVER_5;
import static Funssion.Inforum.domain.score.domain.Score.LIMIT_DAILY_SCORE;
import static Funssion.Inforum.domain.score.service.ScoreService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTest {
    private final long SCORE_ROUND_LIMIT = LIMIT_DAILY_SCORE - 1L;
    @Mock
    ScoreRepository scoreRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    PostRepository postRepository;
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
            UpdatedUserScoreInfo addedScoreAndDailyScore = scoreService.getUpdatedUserScoreInfo(userId, ScoreType.MAKE_MEMO);
            assertThat(addedScoreAndDailyScore.addScore()).isEqualTo(50L);
            assertThat(addedScoreAndDailyScore.updateDailyScore()).isEqualTo(200L);
        }
        @Test
        @DisplayName("유저가 일별 최대 점수를 채우지 않은 경우")
        void addScoreWhenNotOverDailyLimit(){
            when(scoreRepository.getUserDailyScore(userId)).thenReturn(110L);
            UpdatedUserScoreInfo addedScoreAndDailyScore = scoreService.getUpdatedUserScoreInfo(userId, ScoreType.MAKE_MEMO);
            assertThat(addedScoreAndDailyScore.addScore()).isEqualTo(50L);
            assertThat(addedScoreAndDailyScore.updateDailyScore()).isEqualTo(160L);
        }

        @Test
        @DisplayName("유저가 일별 최대 점수를 이미 채운 경우")
        void addScoreWhenAlreadyDailyLimit(){
            when(scoreRepository.getUserDailyScore(userId)).thenReturn(LIMIT_DAILY_SCORE);
            UpdatedUserScoreInfo addedScoreAndDailyScore = scoreService.getUpdatedUserScoreInfo(userId, ScoreType.MAKE_MEMO);
            assertThat(addedScoreAndDailyScore.addScore()).isEqualTo(0L);
            assertThat(addedScoreAndDailyScore.updateDailyScore()).isEqualTo(LIMIT_DAILY_SCORE);
        }

        @Test
        @DisplayName("유저가 행한 행동이 일별 최대 점수를 초과하는 경우")
        void addScoreWhenOverDailyLimit(){
            when(scoreRepository.getUserDailyScore(userId)).thenReturn(SCORE_ROUND_LIMIT);
            UpdatedUserScoreInfo addedScoreAndDailyScore = scoreService.getUpdatedUserScoreInfo(userId, ScoreType.MAKE_MEMO);
            assertThat(addedScoreAndDailyScore.addScore()).isEqualTo(LIMIT_DAILY_SCORE-SCORE_ROUND_LIMIT);
            assertThat(addedScoreAndDailyScore.updateDailyScore()).isEqualTo(LIMIT_DAILY_SCORE);
        }
//        @Test
//        @DisplayName("유저가 점수를 얻었을 때, Rank가 변동되는 경우")
//        void updateRank(){
//            Long Bronze_4_score = 130L;
//            Long userDailyScore = 80L;
//            when(scoreRepository.getUserDailyScore(userId)).thenReturn(userDailyScore);
//            when(scoreRepository.getRank(userId)).thenReturn(Rank.BRONZE_5.toString());
//            when(scoreRepository.updateUserScoreAtDay(userId, ScoreType.MAKE_MEMO.getScore(),userDailyScore + ScoreType.MAKE_MEMO.getScore())).thenReturn(Bronze_4_score);
//            when(scoreRepository.updateRank())
//
//            assertThat(scoreService.checkUserDailyScoreAndAdd(userId,ScoreType.MAKE_MEMO,1L)).isEqualTo(Rank.BRONZE_4);
//        }
        @Test
        @DisplayName("유저가 점수를 얻었을 때, Rank가 변동되는 경우")
        void updateRank(){
            List<Rank> ranks = List.of(Rank.values());
            int currentRankIndex = ranks.indexOf(BRONZE_1);
            Rank beUpdateRank = ranks.get(currentRankIndex + 1);
            assertThat(beUpdateRank).isEqualTo(SILVER_5);
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
            when(scoreRepository.getRank(userId)).thenReturn(Rank.BRONZE_4.toString());

            Assertions.assertThatCode(()->scoreService.subtractUserScore(userId,ScoreType.MAKE_MEMO,1L)).doesNotThrowAnyException();

            verify(scoreRepository, times(1)).updateUserScoreAtOtherDay(userId,-ScoreType.MAKE_MEMO.getScore());
        }
        @Test
        @DisplayName("유저가 어떤 행동으로 점수를 얻은 시점이 그 당일일 때, 그 행동을 되돌리는 경우")
        void minusScoreToday(){
            LocalDateTime now = LocalDateTime.now();
            Rank userRank = Rank.BRONZE_4;
            Score score = Score.builder()
                    .id(1L)
                    .userId(userId)
                    .createdDate(LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),now.getHour(),0))
                    .scoreType(ScoreType.MAKE_MEMO)
                    .score(50L)
                    .postId(1L)
                    .build();
            when(memberRepository.getDailyScore(userId)).thenReturn(LIMIT_DAILY_SCORE);
            when(scoreRepository.getRank(userId)).thenReturn(userRank.toString());
            when(scoreRepository.findScoreHistoryInfoById(userId,score.getScoreType(),score.getPostId())).thenReturn(Optional.ofNullable(score));
            Assertions.assertThatCode(()->scoreService.subtractUserScore(userId,score.getScoreType(),score.getPostId())).doesNotThrowAnyException();

            verify(scoreRepository, times(1)).updateUserScoreAtDay(userId,-ScoreType.MAKE_MEMO.getScore(),LIMIT_DAILY_SCORE - score.getScore());
        }
    }

}