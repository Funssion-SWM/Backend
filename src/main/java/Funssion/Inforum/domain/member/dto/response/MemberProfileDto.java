package Funssion.Inforum.domain.member.dto.response;

import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MemberProfileDto {
    private final Long userId;
    private final String profileImageFilePath;
    private final String nickname;
    private final String introduce;
    private final List<String> userTags;
    private final Long followCnt;
    private final Long followerCnt;
    private Boolean isFollowed;

    public static MemberProfileDto valueOf(MemberProfileEntity memberProfileEntity) {
        return MemberProfileDto.builder()
                .userId(memberProfileEntity.getUserId())
                .profileImageFilePath(memberProfileEntity.getProfileImageFilePath())
                .nickname(memberProfileEntity.getNickname())
                .introduce(memberProfileEntity.getIntroduce())
                .userTags(memberProfileEntity.getUserTags())
                .followCnt(memberProfileEntity.getFollowCnt())
                .followerCnt(memberProfileEntity.getFollowerCnt())
                .isFollowed(Boolean.FALSE)
                .build();
    }

    public void setIsFollowed(Boolean isFollowed) {
        this.isFollowed = isFollowed;
    }
}
