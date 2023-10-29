package Funssion.Inforum.domain.employer.controller;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.employer.dto.EmployeeDto;
import Funssion.Inforum.domain.employer.service.EmployerService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employer")
@RequiredArgsConstructor
public class EmployerController {
    private final EmployerService employerService;
    @GetMapping("/employees")
    public List<EmployeeDto> getEmployeesLookingForJob(@RequestParam(required = false, defaultValue = "0") @Min(0) Long page){
        return employerService.getEmployeesLookingForJob(page);
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
