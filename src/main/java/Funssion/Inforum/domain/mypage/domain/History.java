package Funssion.Inforum.domain.mypage.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class History {
    private Long id;
    private Long userId;
    private Long memoCnt;
    private Long blogCnt;
    private Long qnaCnt;
    private Date date;
}
