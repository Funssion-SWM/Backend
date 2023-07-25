package Funssion.Inforum.domain.member.dto;

import lombok.Getter;

@Getter
public class TokenDto {
    private String token;
    private Long id;

    public TokenDto(String token, Long id){
        this.token = token;
        this.id = id;
    }
}
