package Funssion.Inforum.domain.member.dto.response;

import lombok.Getter;

@Getter
public class isAlreadyExistSocialMember {
    private boolean isExist;
    private Long id;
    public isAlreadyExistSocialMember(boolean isExist, Long id) {
        this.isExist = isExist;
        this.id = id;
    }
}
