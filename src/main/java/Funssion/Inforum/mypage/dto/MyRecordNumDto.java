package Funssion.Inforum.mypage.dto;

import Funssion.Inforum.memo.dto.MemoListDto;
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
