package Funssion.Inforum.domain.mypage.dto;

import Funssion.Inforum.domain.mypage.entity.History;
import lombok.Getter;

import java.util.Date;

@Getter
public class MyRecordNumDto {
    private final Long historyId;
    private final Date date;
    private final Long postCnt;

    public MyRecordNumDto(History history) {
        this.historyId = history.getId();
        this.date = history.getDate();
        this.postCnt = history.getMemoCnt() + history.getBlogCnt() + history.getQnaCnt();
    }
}
