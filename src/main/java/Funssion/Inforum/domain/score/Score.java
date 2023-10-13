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
    private final Long postId;
    private final LocalDateTime createdDate;
}
