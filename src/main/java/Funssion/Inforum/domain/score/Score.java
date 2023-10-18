package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.constant.ScoreType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class Score {
    private final Long id;
    private final Long userId;
    private final ScoreType scoreType;
    private final Long score;
    private final Long postId;
    private final LocalDateTime createdDate;

    public static final Long LIMIT_DAILY_SCORE = 200L;

    public static Long calculateAddingScore(Long userDailyScore, ScoreType scoreType){
        if (scoreType.equals(ScoreType.LIKE) || scoreType.equals(ScoreType.BEST_ANSWER)){
            return scoreType.getScore();
        }
        if(userDailyScore + scoreType.getScore() >= LIMIT_DAILY_SCORE) return LIMIT_DAILY_SCORE - userDailyScore;
        else return scoreType.getScore();
    }
    public static Long calculateDailyScore(Long userDailyScore,ScoreType scoreType){
        if (scoreType.equals(ScoreType.LIKE) || scoreType.equals(ScoreType.BEST_ANSWER)){
            return userDailyScore;
        }

        if(userDailyScore + scoreType.getScore() >= LIMIT_DAILY_SCORE) return LIMIT_DAILY_SCORE;
        else return userDailyScore + scoreType.getScore();
    }
}
