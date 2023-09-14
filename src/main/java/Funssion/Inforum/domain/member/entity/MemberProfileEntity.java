package Funssion.Inforum.domain.member.entity;

import Funssion.Inforum.s3.S3Utils;
import Funssion.Inforum.domain.member.dto.request.MemberInfoDto;
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
    public static MemberProfileEntity generateWithNoProfileImage(MemberInfoDto memberInfoDto){
        return MemberProfileEntity.builder()
                .profileImageFilePath(null)
                .userTags(memberInfoDto.getMemberTags())
                .nickname(memberInfoDto.getNickname())
                .introduce(memberInfoDto.getIntroduce())
                .build();
    }


    public static MemberProfileEntity generateWithProfileImage(MemberInfoDto memberInfoDto,String imagePath){
        return MemberProfileEntity.builder()
                .profileImageFilePath(imagePath)
                .userTags(memberInfoDto.getMemberTags())
                .nickname(memberInfoDto.getNickname())
                .introduce(memberInfoDto.getIntroduce())
                .build();
    }

    public static MemberProfileEntity generateKeepingImagePath(MemberInfoDto memberInfoDto,String imagePath){
        return MemberProfileEntity.builder()
                .profileImageFilePath(imagePath)
                .userTags(memberInfoDto.getMemberTags())
                .nickname(memberInfoDto.getNickname())
                .introduce(memberInfoDto.getIntroduce())
                .build();
    }

}
