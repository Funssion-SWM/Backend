package Funssion.Inforum.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileEntity {
    private String profileImageFilePath;
    private String nickname;
    private String introduce;
    private String tags;

    public static RowMapper<MemberProfileEntity> MemberInfoRowMapper() {
        return ((rs, rowNum) ->
                MemberProfileEntity.builder()
                        .introduce(rs.getString("introduce"))
                        .nickname(rs.getString("name"))
                        .profileImageFilePath(rs.getString("image_path"))
                        .tags(rs.getString("tags"))
                        .build()
        );
    }

}
