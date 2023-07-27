package Funssion.Inforum.domain.member.entity;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class NonSocialMember extends Member {
    private Long authId;
    private String userEmail;
    private String userPw;

}
