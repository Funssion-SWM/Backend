package Funssion.Inforum.domain.member.entity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class Employer extends NonSocialMember{
    private final String userPw;
    private final String companyName;
    private final Boolean isEmployer;
}
