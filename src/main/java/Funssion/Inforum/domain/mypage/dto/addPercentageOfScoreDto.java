package Funssion.Inforum.domain.mypage.dto;

import Funssion.Inforum.domain.mypage.domain.ScoreAndCountDao;
import lombok.Getter;

@Getter
public class addPercentageOfScoreDto extends ScoreAndCountDao{
    private final Float memoPercent;
    private final Float questionPercent;
    private final Float selectingAnswerPercent;
    private final Float answerPercent;
    private final Float commentPercent;
    private final Float likePercent;
    private final Float bestAnswerPercent;

    public addPercentageOfScoreDto(ScoreAndCountDao scoreAndCountDao){
        super(scoreAndCountDao.getMemoScoreAndCount(),scoreAndCountDao.getQuestionScoreAndCount(),scoreAndCountDao.getSelectingAnswerScoreAndCount()
        ,scoreAndCountDao.getAnswerScoreAndCount(),scoreAndCountDao.getCommentScoreAndCount(),scoreAndCountDao.getLikeScoreAndCount(),scoreAndCountDao.getBestAnswerScoreAndCount());
        Long totalScore = addAllScores(scoreAndCountDao);
        this.memoPercent = (float) scoreAndCountDao.getMemoScoreAndCount().getScore() / totalScore;
        this.questionPercent = (float) scoreAndCountDao.getQuestionScoreAndCount().getScore() / totalScore;
        this.selectingAnswerPercent = (float) scoreAndCountDao.getSelectingAnswerScoreAndCount().getScore() / totalScore;
        this.answerPercent = (float) scoreAndCountDao.getAnswerScoreAndCount().getScore() / totalScore;
        this.commentPercent = (float) scoreAndCountDao.getCommentScoreAndCount().getScore() / totalScore;
        this.likePercent = (float) scoreAndCountDao.getLikeScoreAndCount().getScore() / totalScore;
        this.bestAnswerPercent = (float) scoreAndCountDao.getBestAnswerScoreAndCount().getScore() / totalScore;
    }
    private Long addAllScores(ScoreAndCountDao scoreAndCountDao){
        return scoreAndCountDao.getAnswerScoreAndCount().getScore()
                + scoreAndCountDao.getLikeScoreAndCount().getScore()
                + scoreAndCountDao.getCommentScoreAndCount().getScore()
                + scoreAndCountDao.getMemoScoreAndCount().getScore()
                + scoreAndCountDao.getQuestionScoreAndCount().getScore()
                + scoreAndCountDao.getBestAnswerScoreAndCount().getScore()
                + scoreAndCountDao.getSelectingAnswerScoreAndCount().getScore();
    }
}
