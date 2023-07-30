package Funssion.Inforum.domain.member.entity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class NonSocialMember extends Member {
    private final Long authId;
    private final String userEmail;
    private final String userPw;

}
