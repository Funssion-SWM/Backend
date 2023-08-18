package Funssion.Inforum.domain.member.entity;

import lombok.Getter;

@Getter
public class IsRegisteredOAUth2Member{
    Boolean isAlreadyRegistered;
    Long userId;
    public IsRegisteredOAUth2Member(Boolean isAlreadyRegistered, Long userId) {
        this.isAlreadyRegistered = isAlreadyRegistered;
        this.userId = userId;
    }
}