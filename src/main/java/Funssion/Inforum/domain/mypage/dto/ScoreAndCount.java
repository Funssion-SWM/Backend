package Funssion.Inforum.domain.mypage.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScoreAndCount{
    private final Long score;
    private final Long count;
}