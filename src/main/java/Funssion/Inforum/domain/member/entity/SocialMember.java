package Funssion.Inforum.domain.member.entity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SocialMember extends Member {
    private final Long id;
    private final String userEmail;
}
