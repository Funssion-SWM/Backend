package Funssion.Inforum.domain.score;

import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;


    @Transactional
    public Rank checkUserDailyScoreAndAdd(Long userId, ScoreType scoreType,Long postId){
        UpdatedUserScoreInfo updatedUserScoreInfo = getUpdatedUserScoreInfo(userId, scoreType);
        Rank beforeRank = Rank.valueOf(scoreRepository.getRank(userId));
        if(doesUserScoreModify(updatedUserScoreInfo.addScore())){
            scoreRepository.saveScoreHistory(userId, scoreType, updatedUserScoreInfo.addScore(), postId);
            if(doesRankUpAfterUpdating(userId, updatedUserScoreInfo,beforeRank)){
                Rank updatedRank = updateRank(userId, beforeRank, true);
                postRepository.updateRankOfAllPostTypeAndNotification(updatedRank,postId);
            }
        }
        return beforeRank;
    }

    private Rank updateRank(Long userId, Rank beforeRank, boolean isLevelUp) {
        List<Rank> ranks = List.of(Rank.values());
        int currentRankIndex = ranks.indexOf(beforeRank);
        int updatedRankIndex = isLevelUp? currentRankIndex + 1: currentRankIndex - 1;
        Rank beUpdateRank = ranks.get(updatedRankIndex);
        return scoreRepository.updateRank(beUpdateRank, userId);
    }

    private boolean doesRankUpAfterUpdating(Long userId, UpdatedUserScoreInfo currentUserScoreInfo, Rank rank) {
        return getUserScoreAfterUpdatingScore(userId, currentUserScoreInfo) >= rank.getMax();
    }

    private Long getUserScoreAfterUpdatingScore(Long userId, UpdatedUserScoreInfo result){
        return scoreRepository.updateUserScoreAtDay(userId, result.addScore(), result.updateDailyScore());
    }
    public UpdatedUserScoreInfo getUpdatedUserScoreInfo(Long userId, ScoreType scoreType) {
        Long currentUserDailyScore = scoreRepository.getUserDailyScore(userId);
        Long updateDailyScore = Score.calculateDailyScore(currentUserDailyScore,scoreType);

        Long addScore = Score.calculateAddingScore(currentUserDailyScore,scoreType);
        return new UpdatedUserScoreInfo(updateDailyScore, addScore);
    }

    public record UpdatedUserScoreInfo (Long updateDailyScore, Long addScore) {
    }

    @Transactional
    public void subtractUserScore(Long userId, ScoreType scoreType, Long postId){
        Long dailyScore = memberRepository.getDailyScore(userId);
        Optional<Score> scoreHistoryInfoById = scoreRepository.findScoreHistoryInfoById(userId, scoreType, postId);

        scoreHistoryInfoById.ifPresent(
                scoreOfHistory -> {
                    Rank beforeRank = Rank.valueOf(scoreRepository.getRank(userId));
                    if(doesRankDownAfterUpdating(userId, dailyScore, scoreOfHistory, beforeRank)){
                        Rank updatedRank = updateRank(userId, beforeRank, false);
                        postRepository.updateRankOfAllPostTypeAndNotification(updatedRank,postId);
                    }
                    handleSpecialCaseOfComment(userId, scoreType, scoreHistoryInfoById);
                }
        );

    }

    private boolean doesRankDownAfterUpdating(Long userId, Long dailyScore, Score scoreOfHistory, Rank beforeRank) {
        return minusUserScoreWhetherTodayOrNot(userId, dailyScore, scoreOfHistory) < beforeRank.getMax() - beforeRank.getInterval();
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

    private boolean doesUserScoreModify(Long addScore) {
        return addScore != 0;
    }


}
