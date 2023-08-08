package Funssion.Inforum.domain.mypage.dto;

import Funssion.Inforum.domain.mypage.entity.History;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class MyRecordNumDto {
    private Long historyId;
    private Date date;
    private Long postCnt;

    public MyRecordNumDto(History history) {
        this.historyId = history.getId();
        this.date = history.getDate();
        this.postCnt = history.getMemoCnt() + history.getBlogCnt() + history.getQnaCnt();
    }
}
