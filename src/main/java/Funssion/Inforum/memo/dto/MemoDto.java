package Funssion.Inforum.memo.dto;

import lombok.Builder;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;

@Data
@Builder
public class MemoDto {
    private int memoId;
    private int userId;
    private String userName;
    private String memoTitle;
    private String memoDescription;
    private String memoText;
    private String memoColor;
    private Date createdDate;
    private Date updatedDate;

    public static RowMapper<MemoDto> memoRowMapper() {
        return ((rs, rowNum) ->
                MemoDto.builder()
                        .memoId(rs.getInt("memo_id"))
                        .userId(rs.getInt("user_id"))
                        .userName(rs.getString("user_name"))
                        .memoTitle(rs.getString("memo_title"))
                        .memoText(rs.getString("memo_text"))
                        .memoDescription(rs.getString("memo_description"))
                        .memoColor(rs.getString("memo_color"))
                        .createdDate(rs.getDate("created_date"))
                        .updatedDate(rs.getDate("updated_date"))
                        .build()
        );
    }
}
