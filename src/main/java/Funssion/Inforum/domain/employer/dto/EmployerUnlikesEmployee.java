package Funssion.Inforum.domain.employer.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public class EmployerUnlikesEmployee {
    private final Long employerId;
    private final Long employeeId;
}
