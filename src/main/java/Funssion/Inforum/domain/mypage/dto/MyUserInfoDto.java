package Funssion.Inforum.domain.mypage.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.jdbc.core.RowMapper;

@Getter
@Builder
public class MyUserInfoDto {
    private String userName;

    public static RowMapper<MyUserInfoDto> myUserInfoDtoRowMapper() {
        return ((rs, rowNum) ->
                MyUserInfoDto.builder()
                        .userName(rs.getString("user_name"))
                        .build());
    }
}
