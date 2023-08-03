package Funssion.Inforum.domain.member.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SuccessEmailSendDto {
    private final boolean isSuccess;
}
