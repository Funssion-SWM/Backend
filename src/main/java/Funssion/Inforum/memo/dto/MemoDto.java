package Funssion.Inforum.memo.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class MemoDto {
    private int memoId;
    private int userId;
    private String userName;
    private String memoTitle;
    private String memoText;
    private String memoColor;
    private Date createdDate;
    private Date updatedDate;
}
