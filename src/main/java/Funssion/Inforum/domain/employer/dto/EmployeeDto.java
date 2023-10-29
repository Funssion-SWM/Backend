package Funssion.Inforum.domain.employer.dto;

import Funssion.Inforum.domain.employer.domain.Employee;
import lombok.Getter;

@Getter
public class EmployeeDto {
    private final Long userId;
    private final String username;
    private final String userImagePath;
    private final String rank;
    private final Long score;
    private final Boolean isLike;

    public EmployeeDto(Employee employee){
        this.userId = employee.getUserId();
        this.username = employee.getUsername();
        this.userImagePath = employee.getImagePath();
        this.rank = employee.getRank();
        this.score = employee.getScore();
        this.isLike = employee.getIsLike();
    }
}
