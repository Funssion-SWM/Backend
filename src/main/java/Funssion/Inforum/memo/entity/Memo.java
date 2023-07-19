package Funssion.Inforum.memo.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;


@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Memo {
    private int memoId;
    private int userId;
    private String userName;
    private String memoTitle;
    private String memoDescription;
    private String memoText;
    private String memoColor;
    private Date createdDate;
    private Date updatedDate;
}