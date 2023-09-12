package Funssion.Inforum.domain.member.dto.response;

import lombok.Getter;

@Getter
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private Long id;

    public TokenDto(String accessToken,String refreshToken, Long id){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
    }
}
