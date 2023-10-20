package Funssion.Inforum.domain.score.dto;

import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class UserInfoWithScoreRank {
    private final MemberProfileEntity memberProfileEntity;
    private final ScoreRank scoreRank;
}
