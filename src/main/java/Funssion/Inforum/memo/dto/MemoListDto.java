package Funssion.Inforum.memo.dto;

import lombok.Builder;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;

@Data
@Builder
public class MemoListDto {
    private int memoId;
    private String memoTitle;
    private String memoText;
    private String memoColor;
    private Date createdDate;
    private int authorId;
    private String authorName;
}
