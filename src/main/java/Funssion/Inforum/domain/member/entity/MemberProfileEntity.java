package Funssion.Inforum.domain.member.entity;

import Funssion.Inforum.domain.tag.TagUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileEntity {
    private String profileImageFilePath;
    private String nickname;
    private String introduce;
    private List<String> userTags;

    public static RowMapper<MemberProfileEntity> MemberInfoRowMapper() {
        return ((rs, rowNum) ->
                MemberProfileEntity.builder()
                        .introduce(rs.getString("introduce"))
                        .nickname(rs.getString("name"))
                        .profileImageFilePath(rs.getString("image_path"))
                        .userTags(TagUtils.createStringListFromArray(rs.getArray("tags")))
                        .build()
        );
    }

}
