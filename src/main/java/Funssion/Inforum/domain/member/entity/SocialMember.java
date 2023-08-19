package Funssion.Inforum.domain.member.entity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class SocialMember extends Member {

    public static SocialMember createSocialMember(String email, String nickname) {
        return SocialMember.builder()
                .userEmail(email)
                .userName(nickname)
                .createdDate(LocalDateTime.now())
                .build();
    }
}
