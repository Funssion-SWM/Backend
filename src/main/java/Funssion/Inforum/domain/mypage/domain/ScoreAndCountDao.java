package Funssion.Inforum.domain.mypage.domain;

import Funssion.Inforum.domain.mypage.dto.ScoreAndCount;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class ScoreAndCountDao{
    private final ScoreAndCount memoScoreAndCount;
    private final ScoreAndCount questionScoreAndCount;
    private final ScoreAndCount selectingAnswerScoreAndCount;
    private final ScoreAndCount answerScoreAndCount;
    private final ScoreAndCount commentScoreAndCount;
    private final ScoreAndCount likeScoreAndCount;
    private final ScoreAndCount bestAnswerScoreAndCount;

}