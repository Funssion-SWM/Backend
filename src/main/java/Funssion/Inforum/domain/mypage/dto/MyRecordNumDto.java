package Funssion.Inforum.domain.mypage.dto;

import Funssion.Inforum.domain.mypage.domain.History;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MyRecordNumDto {
    private final Long historyId;
    private final LocalDate date;
    private final Long memoCnt;
    private final Long blogCnt;
    private final Long questionCnt;
    private final Long answerCnt;

    public MyRecordNumDto(History history) {
        this.historyId = history.getId();
        this.date = history.getDate();
        this.memoCnt = history.getMemoCnt();
        this.blogCnt = history.getBlogCnt();
        this.questionCnt = history.getQuestionCnt();
        this.answerCnt = history.getAnswerCnt();
    }
}
