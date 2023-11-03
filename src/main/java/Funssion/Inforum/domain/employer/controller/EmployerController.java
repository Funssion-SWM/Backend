package Funssion.Inforum.domain.employer.controller;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.employer.domain.Employee;
import Funssion.Inforum.domain.employer.domain.EmployeeWithStatus;
import Funssion.Inforum.domain.employer.domain.InterviewResult;
import Funssion.Inforum.domain.employer.service.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employer")
@RequiredArgsConstructor
public class EmployerController {
    private final EmployerService employerService;

    @GetMapping("/employees")
    public List<Employee> getEmployeesListOfInterview(@RequestParam Boolean done){
        return employerService.getEmployeesOfInterview(done);
    }
    @GetMapping("/interview-result/{employeeId}")
    public InterviewResult getResultOfInterview(@PathVariable Long employeeId){
        return employerService.getResultOfInterview(employeeId);
    }

    @GetMapping("/like/employees")
    public List<EmployeeWithStatus> getLikeEmployees(){
        return employerService.getLikeEmployees();
    }

    @PostMapping("/like/{userId}")
    public IsSuccessResponseDto likeEmployee(@PathVariable Long userId){
        employerService.likeEmployee(userId);
        return new IsSuccessResponseDto(true, "관심 지원자로 등록하였습니다.");
    }
    @DeleteMapping("/like/{userId}")
    public IsSuccessResponseDto unlikeEmployee(@PathVariable Long userId){
        employerService.unlikesEmployee(userId);
        return new IsSuccessResponseDto(true, "관심 지원자에서 삭제하였습니다.");
    }

}
