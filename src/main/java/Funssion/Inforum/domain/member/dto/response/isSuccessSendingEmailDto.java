package Funssion.Inforum.domain.member.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class isSuccessSendingEmailDto {
    private final Boolean isSuccess;
    private final String message;
}
