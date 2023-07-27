package Funssion.Inforum.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValidMemberDto {
    private Long id;
    private Boolean isLogin;
}
