package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static Funssion.Inforum.domain.score.Score.LIMIT_DAILY_SCORE;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public Long checkUserDailyScoreAndAdd(Long userId, ScoreType scoreType,Long postId){
        updateData result = getAddedScoreAndDailyScore(userId, scoreType);
        if(isChangedUserScore(result.addScore())){
            scoreRepository.saveScoreHistory(userId,scoreType,postId, result.addScore());
            return scoreRepository.updateUserScoreAtDay(userId, result.addScore(), result.updateDailyScore());
        }
        return 0L;
    }

    public updateData getAddedScoreAndDailyScore(Long userId, ScoreType scoreType) {
        Long scoreOfScoreType = scoreType.getScore();
        Long currentUserDailyScore = scoreRepository.getUserDailyScore(userId);
        Long addedUserDailyScore = currentUserDailyScore + scoreOfScoreType;
        Long updateDailyScore = (addedUserDailyScore > LIMIT_DAILY_SCORE) ? LIMIT_DAILY_SCORE : addedUserDailyScore;

        Long addScore = updateDailyScore - currentUserDailyScore;
        updateData result = new updateData(updateDailyScore, addScore);
        return result;
    }

    public record updateData(Long updateDailyScore, Long addScore) {
    }

    @Transactional
    public void subtractUserScore(Long userId, ScoreType scoreType, Long postId){
        Long dailyScore = memberRepository.getDailyScore(userId);
        scoreRepository.findScoreHistoryInfoById(userId, scoreType, postId).ifPresent(
                scoreOfHistory -> {
                    updateUserScoreWhetherTodayOrNot(userId, scoreType, postId, dailyScore, scoreOfHistory);
                }
        );
    }

    public Long updateUserScoreWhetherTodayOrNot(Long userId, ScoreType scoreType, Long postId, Long dailyScore, Score scoreOfHistory) {
        scoreRepository.deleteScoreHistory(userId, scoreType, postId);
        if(isDeleteToday(scoreOfHistory)) {
            return scoreRepository.updateUserScoreAtDay(userId, scoreOfHistory.getScore(), dailyScore - scoreOfHistory.getScore());
        }
        return scoreRepository.updateUserScoreAtOtherDay(userId, scoreOfHistory.getScore());
    }
    public Long getScore(Long userId){
        return scoreRepository.getScore(userId);
    }

    @Scheduled(cron = "0 00 00 * * ?")
    public void scheduledForInitializingUserDailyScore(){
        scoreRepository.initializeAllUsersDailyScore();
    }

    private boolean isDeleteToday(Score scoreOfHistory) {
        return scoreOfHistory.getCreatedDate().toLocalDate().toEpochDay() == LocalDate.now().toEpochDay();
    }

    private boolean isChangedUserScore(Long addScore) {
        return addScore != 0;
    }


}
