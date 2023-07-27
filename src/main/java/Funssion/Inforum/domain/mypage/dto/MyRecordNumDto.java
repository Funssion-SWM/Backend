package Funssion.Inforum.domain.mypage.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.util.Date;

@Data
@Builder
public class MyRecordNumDto {

    private Date date;
    private int postCnt;

    public static RowMapper<MyRecordNumDto> myRecordNumRowMapper() {
        return ((rs, rowNum) ->
                MyRecordNumDto.builder()
                        .date(rs.getDate("date"))
                        .postCnt(rs.getInt("post_cnt"))
                        .build());
    }
}
