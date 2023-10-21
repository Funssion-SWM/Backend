package Funssion.Inforum.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyScoreActivityCountDto {
    private final Long memo;
    private final Long question;
    private final Long selectingAnswer;
    private final Long answer;
    private final Long like;
    private final Long comment;
    private final Long recomment;
    private final Long bestAnswer;

}
