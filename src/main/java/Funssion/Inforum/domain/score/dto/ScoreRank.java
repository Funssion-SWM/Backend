package Funssion.Inforum.domain.score.dto;

import Funssion.Inforum.domain.score.Rank;
import lombok.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreRank {
    private Long score;
    private Rank rank;
    private Long dailyScore;
}
