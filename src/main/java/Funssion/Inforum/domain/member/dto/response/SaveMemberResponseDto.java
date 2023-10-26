package Funssion.Inforum.domain.member.dto.response;

import Funssion.Inforum.domain.member.constant.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;


@AllArgsConstructor
@Builder
@Getter
@ToString
public class SaveMemberResponseDto {
    private final Long id;
    private final String name;
    private final LoginType loginType;
    private final LocalDateTime createdDate;
    private final String email;
    private final String role;
}
