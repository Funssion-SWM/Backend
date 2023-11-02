package Funssion.Inforum.domain.interview.controller;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.interview.constant.InterviewStatus;
import Funssion.Inforum.domain.interview.dto.InterviewAnswerDto;
import Funssion.Inforum.domain.interview.dto.InterviewQuestionDto;
import Funssion.Inforum.domain.interview.dto.QuestionsDto;
import Funssion.Inforum.domain.interview.exception.InterviewForbiddenException;
import Funssion.Inforum.domain.interview.service.InterviewService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
public class InterviewController {
    private final InterviewService interviewService;
    @PostMapping("/questions/{employeeId}")
    public IsSuccessResponseDto saveInterviewQuestions(@PathVariable Long employeeId, @RequestBody @Valid QuestionsDto questionsDto){
        return interviewService.saveQuestionsAndNotifyInterview(employeeId,questionsDto);
    }
    @GetMapping("/questions/{employerId}/{employeeId}")
    public InterviewQuestionDto getInterviewQuestion(@PathVariable Long employerId, @PathVariable Long employeeId){
        return interviewService.getInterviewInfoTo(employeeId, employerId);
    }
    @ApiResponse(description="1번 문제를 봤을 때 호출됩니다. 이에 따라 status가 1번문제 보는중으로 바뀜")
    @PutMapping("/start/{employerId}")
    public InterviewStatus startInterviewByEmployee(@PathVariable Long employerId){
        Long userId = SecurityContextUtils.getAuthorizedUserId();

        return interviewService.startInterview(employerId,userId);
    }
    @ApiResponse(description="답변을 제출할때마다 호출됩니다. 이에따라 다음문제 보는중으로 바뀜. ex) 1번 답변 제출시 2번문제 보는중으로 바뀜")
    @PostMapping("/answers")
    public IsSuccessResponseDto saveAnswerOfQuestion(@RequestBody InterviewAnswerDto interviewAnswerDto){
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        if(!interviewService.authorizeInterview(interviewAnswerDto.getEmployerId(),userId)) throw new InterviewForbiddenException();

        interviewService.saveAnswerOfQuestion(interviewAnswerDto,userId);
        return new IsSuccessResponseDto(true, interviewAnswerDto.getQuestionNumber() + "번 질문의 답변이 성공적으로 등록되었습니다.");
    }
}
