package Funssion.Inforum.domain.interview.service;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.exception.etc.DuplicateException;
import Funssion.Inforum.domain.interview.constant.InterviewStatus;
import Funssion.Inforum.domain.interview.domain.Interview;
import Funssion.Inforum.domain.interview.dto.InterviewAnswerDto;
import Funssion.Inforum.domain.interview.dto.InterviewQuestionDto;
import Funssion.Inforum.domain.interview.dto.QuestionsDto;
import Funssion.Inforum.domain.interview.repository.InterviewRepository;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final MemberRepository memberRepository;
    public IsSuccessResponseDto saveQuestionsAndNotifyInterview(Long employeeId, QuestionsDto questionsDto){
        if(interviewRepository.findIfAlreadyInterviewing(employeeId)) throw new DuplicateException("이미 면접요청을 보낸 지원자입니다.");

        interviewRepository.saveQuestions(employeeId,questionsDto);
        // ToDo : notification

        return new IsSuccessResponseDto(true, "성공적으로 면접알림을 전송하였습니다.");
    }


    public InterviewQuestionDto getInterviewInfoTo(Long employeeId){
        Interview interviewInfo = interviewRepository.getInterviewQuestionOf(employeeId);
        return new InterviewQuestionDto(interviewInfo,memberRepository.getCompanyName(interviewInfo.getEmployerId()));
    }

    public InterviewStatus startInterview(Long employerId, Long employeeId){
        return interviewRepository.startInterview(employerId, employeeId);
    }
    public InterviewStatus saveAnswerOfQuestion(InterviewAnswerDto interviewAnswerDto, Long userId) {
        // 풀고있는 상태와 다른 답변 문제가 제출될 경우의 에러처리
        handlingOfMismatchWithStatus(interviewAnswerDto,userId);

        return interviewRepository.saveAnswerOfQuestion(interviewAnswerDto,userId);
    }
    public Boolean authorizeInterview(Long employerId,Long employeeId){
        return interviewRepository.isAuthorizedInterview(employerId,employeeId);
    }
    private void handlingOfMismatchWithStatus(InterviewAnswerDto interviewAnswerDto,Long userId){
        if (interviewRepository.isMismatchWithStatus(interviewAnswerDto,userId)) throw new BadRequestException("잘못된 문제로 요청을 보내고 있습니다. 현 문제번호와 답변을 확인해주세요.");
    }
}
