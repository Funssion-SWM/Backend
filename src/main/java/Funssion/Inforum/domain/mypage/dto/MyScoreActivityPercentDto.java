package Funssion.Inforum.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyScoreActivityPercentDto {
    private final float myMemosCount;
    private final float myQuestionsCount;
    private final float mySelectingAnswerCount;
    private final float myAnswersCount;
    private final float myTakenLikesCount;
    private final float myCommentsCount;
    private final float myRecommentsCount;
    private final float myBestAnswerCount;
}
