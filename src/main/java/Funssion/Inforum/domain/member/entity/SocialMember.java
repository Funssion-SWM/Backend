package Funssion.Inforum.domain.member.entity;

import Funssion.Inforum.domain.member.constant.LoginType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SocialMember extends Member {

    public static SocialMember createSocialMember(String email, String nickname) {
        return SocialMember.builder()
                .userEmail(email)
                .userName(nickname)
                .loginType(LoginType.SOCIAL)
                .build();
    }
}
