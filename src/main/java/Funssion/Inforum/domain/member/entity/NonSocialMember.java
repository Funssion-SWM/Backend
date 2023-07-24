package Funssion.Inforum.domain.member.entity;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class NonSocialMember extends Member {
    private Long auth_id;
    private String user_email;
    private String user_pw;

}
