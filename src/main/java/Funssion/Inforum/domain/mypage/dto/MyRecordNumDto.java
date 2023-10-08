package Funssion.Inforum.domain.mypage.dto;

import Funssion.Inforum.domain.mypage.domain.History;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MyRecordNumDto {
    private final Long historyId;
    private final LocalDate date;
    private final Long postCnt;

    public MyRecordNumDto(History history) {
        this.historyId = history.getId();
        this.date = history.getDate();
        this.postCnt = history.getMemoCnt() + history.getBlogCnt() + history.getQuestionCnt() + history.getAnswerCnt();
    }
}
