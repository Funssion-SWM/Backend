package Funssion.Inforum.domain.interview.service;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.exception.etc.DuplicateException;
import Funssion.Inforum.domain.interview.constant.InterviewStatus;
import Funssion.Inforum.domain.interview.domain.Interview;
import Funssion.Inforum.domain.interview.dto.InterviewAnswerDto;
import Funssion.Inforum.domain.interview.dto.InterviewQuestionDto;
import Funssion.Inforum.domain.interview.dto.QuestionsDto;
import Funssion.Inforum.domain.interview.exception.InterviewForbiddenException;
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


    public InterviewQuestionDto getInterviewInfoTo(Long employeeId, Long employerId){
        Interview interviewInfo = interviewRepository.getInterviewQuestionOf(employeeId, employerId);
        return new InterviewQuestionDto(interviewInfo,memberRepository.getCompanyName(interviewInfo.getEmployerId()));
    }

    public InterviewStatus startInterview(Long employerId, Long employeeId){
        if (!authorizeInterview(employerId,employeeId)) throw new InterviewForbiddenException();

        return interviewRepository.startInterview(employerId, employeeId);
    }
    public InterviewStatus saveAnswerOfQuestion(InterviewAnswerDto interviewAnswerDto, Long userId) {
        // 풀고있는 상태와 다른 답변 문제가 제출될 경우의 에러처리
        handlingOfMismatchWithStatus(interviewAnswerDto,userId);

        interviewRepository.saveAnswerOfQuestion(interviewAnswerDto,userId);

        InterviewStatus interviewStatusAfterAnswer = getInterviewStatus(interviewAnswerDto);
        return interviewRepository.updateStatus(interviewAnswerDto.getEmployerId(),userId,interviewStatusAfterAnswer);

    }
    public InterviewStatus updateStatus(Long employerId, Long employeeId){
        InterviewStatus afterStatus = switch(interviewRepository.getInterviewStatusOfUser(employerId, employeeId)){
            case ING_Q1 -> InterviewStatus.ING_Q2;
            case ING_Q2 -> InterviewStatus.ING_Q3;
            case ING_Q3 -> InterviewStatus.DONE;
            case READY -> throw new BadRequestException("READY 상태에서 update status를 호출할 수 없습니다.");
            case DONE ->  throw new BadRequestException("DONE 상태에서 update status를 호출할 수 없습니다.");
        };
        return interviewRepository.updateStatus(employerId, employeeId, afterStatus);
    }

    private static InterviewStatus getInterviewStatus(InterviewAnswerDto interviewAnswerDto) {
        InterviewStatus interviewStatusAfterAnswer = switch (interviewAnswerDto.getQuestionNumber()) {
            case 1 -> InterviewStatus.ING_Q2;
            case 2 -> InterviewStatus.ING_Q3;
            case 3 -> InterviewStatus.DONE;
            default -> throw new BadRequestException("인터뷰 답변객체의 번호가 '1','2','3' 이 아닙니다.");
        };
        return interviewStatusAfterAnswer;
    }

    public Boolean authorizeInterview(Long employerId,Long employeeId){
        return interviewRepository.isAuthorizedInterview(employerId,employeeId);
    }
    private void handlingOfMismatchWithStatus(InterviewAnswerDto interviewAnswerDto,Long userId){
        if (interviewRepository.isMismatchWithStatus(interviewAnswerDto,userId)) throw new BadRequestException("잘못된 문제로 요청을 보내고 있습니다. 현 문제번호와 답변을 확인해주세요.");
    }
}
