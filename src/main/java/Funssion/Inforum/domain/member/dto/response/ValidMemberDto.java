package Funssion.Inforum.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValidMemberDto {
    private Long id;
    private Boolean isLogin;
    private String authority;
}
