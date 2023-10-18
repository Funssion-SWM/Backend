package Funssion.Inforum.domain.mypage.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class MyRankScoreDto {
    private final String myRank;
    private final Long myScore;
    private final Integer rankInterval;
    private final Long rankMaxScore;
}
