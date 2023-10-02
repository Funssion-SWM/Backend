package Funssion.Inforum.domain.mypage.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class History {
    private Long id;
    private Long userId;
    private Long memoCnt;
    private Long blogCnt;
    private Long questionCnt;
    private Long answerCnt;
    private LocalDate date;
}
