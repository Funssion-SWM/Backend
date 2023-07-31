package Funssion.Inforum.domain.member.entity;

import Funssion.Inforum.domain.member.dto.MemberSaveDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class NonSocialMember extends Member {
    private final Long authId;
    private final String userPw;

    public static NonSocialMember createNonSocialMember(MemberSaveDto memberSaveDto){
        return NonSocialMember.builder()
                .userName(memberSaveDto.getUserName())
                .loginType(memberSaveDto.getLoginType())
                .userEmail(memberSaveDto.getUserEmail())
                .userPw(memberSaveDto.getUserPw())
                .build();
    }

}
