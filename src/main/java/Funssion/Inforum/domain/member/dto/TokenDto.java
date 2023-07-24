package Funssion.Inforum.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {
    private String token;
    private Long id;
    public TokenDto(String token){
        this.token = token;
    }

    public TokenDto(String token, Long id){
        this.token = token;
        this.id = id;
    }
}
