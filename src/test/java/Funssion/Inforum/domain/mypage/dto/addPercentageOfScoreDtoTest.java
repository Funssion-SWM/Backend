package Funssion.Inforum.domain.mypage.dto;

import Funssion.Inforum.domain.mypage.domain.ScoreAndCountDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class addPercentageOfScoreDtoTest {
    @Test
    @DisplayName("점수 퍼센트 로직 확인")
    void checkScorePercent(){
        ScoreAndCountDao mockDao = ScoreAndCountDao.builder()
                .answerScoreAndCount(new ScoreAndCount(1L, 1L))
                .bestAnswerScoreAndCount(new ScoreAndCount(2L, 2L))
                .likeScoreAndCount(new ScoreAndCount(3L, 3L))
                .memoScoreAndCount(new ScoreAndCount(4L, 4L))
                .questionScoreAndCount(new ScoreAndCount(5L, 5L))
                .commentScoreAndCount(new ScoreAndCount(6L, 6L))
                .selectingAnswerScoreAndCount(new ScoreAndCount(7L, 7L))
                .build();

        long totalScores = 1L + 2L + 3L + 4L + 5L + 6L + 7L;
        addPercentageOfScoreDto addPercentageOfScoreDto = new addPercentageOfScoreDto(mockDao);
        assertThat(addPercentageOfScoreDto.getAnswerPercent()).isEqualTo((float) mockDao.getAnswerScoreAndCount().getScore() / totalScores);
        assertThat(addPercentageOfScoreDto.getQuestionPercent()).isEqualTo((float) mockDao.getQuestionScoreAndCount().getScore() / totalScores);
        assertThat(addPercentageOfScoreDto.getMemoPercent()).isEqualTo((float) mockDao.getMemoScoreAndCount().getScore() / totalScores);
        assertThat(addPercentageOfScoreDto.getBestAnswerPercent()).isEqualTo((float) mockDao.getBestAnswerScoreAndCount().getScore() / totalScores);
        assertThat(addPercentageOfScoreDto.getSelectingAnswerPercent()).isEqualTo((float) mockDao.getSelectingAnswerScoreAndCount().getScore() / totalScores);
        assertThat(addPercentageOfScoreDto.getCommentPercent()).isEqualTo((float) mockDao.getCommentScoreAndCount().getScore() / totalScores);
        assertThat(addPercentageOfScoreDto.getLikePercent()).isEqualTo((float) mockDao.getLikeScoreAndCount().getScore() / totalScores);
    }
}