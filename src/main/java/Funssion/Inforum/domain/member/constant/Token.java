package Funssion.Inforum.domain.member.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum Token {
    ACCESS_TOKEN("accessToken"),
    REFRESH_TOKEN("refreshToken");
    private final String type;
}
