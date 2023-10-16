package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;


    @Transactional
    public Long checkUserDailyScoreAndAdd(Long userId, ScoreType scoreType,Long postId){
        updateData result = getAddedScoreAndDailyScore(userId, scoreType);
        if(isChangedUserScore(result.addScore())){
            scoreRepository.saveScoreHistory(userId, scoreType, result.addScore(), postId);
            return scoreRepository.updateUserScoreAtDay(userId, result.addScore(), result.updateDailyScore());
        }
        return 0L;
    }
    public updateData getAddedScoreAndDailyScore(Long userId, ScoreType scoreType) {
        Long currentUserDailyScore = scoreRepository.getUserDailyScore(userId);
        Long updateDailyScore = Score.calculateDailyScore(currentUserDailyScore,scoreType);

        Long addScore = Score.calculateAddingScore(currentUserDailyScore,scoreType);
        updateData result = new updateData(updateDailyScore, addScore);
        return result;
    }

    public record updateData(Long updateDailyScore, Long addScore) {
    }

    @Transactional
    public void subtractUserScore(Long userId, ScoreType scoreType, Long postId){
        Long dailyScore = memberRepository.getDailyScore(userId);
        Optional<Score> scoreHistoryInfoById = scoreRepository.findScoreHistoryInfoById(userId, scoreType, postId);

        scoreHistoryInfoById.ifPresent(
                scoreOfHistory -> {
                    minusUserScoreWhetherTodayOrNot(userId, dailyScore, scoreOfHistory);
                    handleSpecialCaseOfComment(userId, scoreType, scoreHistoryInfoById);
                }
        );

    }

    private void handleSpecialCaseOfComment(Long userId, ScoreType scoreType, Optional<Score> scoreHistoryInfoById) {
        if(scoreType == ScoreType.MAKE_COMMENT){
            commentRepository.findIfUserRegisterAnotherCommentOfPost(userId, scoreHistoryInfoById.get().getPostId()).ifPresent(anotherComment ->
                    checkUserDailyScoreAndAdd(userId,ScoreType.MAKE_COMMENT,anotherComment.getId())
            );
        }
    }


    public Long minusUserScoreWhetherTodayOrNot(Long userId, Long dailyScore, Score scoreOfHistory) {
        scoreRepository.deleteScoreHistory(scoreOfHistory);
        if(isDeleteToday(scoreOfHistory)) {
            return scoreRepository.updateUserScoreAtDay(userId, -scoreOfHistory.getScore(), dailyScore - scoreOfHistory.getScore());
        }
        return scoreRepository.updateUserScoreAtOtherDay(userId, -scoreOfHistory.getScore());
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
