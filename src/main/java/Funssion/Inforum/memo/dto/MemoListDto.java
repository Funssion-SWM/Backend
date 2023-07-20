package Funssion.Inforum.memo.dto;

import lombok.Builder;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;

@Data
@Builder
public class MemoListDto {
    private int memoId;
    private String memoTitle;
    private String memoText;
    private String memoDescription;
    private String memoColor;
    private Date createdDate;
    private int authorId;
    private String authorName;

    public static RowMapper<MemoListDto> memoListRowMapper() {
        return ((rs, rowNum) ->
                MemoListDto.builder()
                        .memoId(rs.getInt("memo_id"))
                        .memoTitle(rs.getString("memo_title"))
                        .memoDescription(rs.getString("memo_description"))
                        .memoText(rs.getString("memo_text"))
                        .memoColor(rs.getString("memo_color"))
                        .createdDate(rs.getDate("created_date"))
                        .authorId(rs.getInt("user_id"))
                        .authorName(rs.getString("user_name"))
                        .build());
    }
}
