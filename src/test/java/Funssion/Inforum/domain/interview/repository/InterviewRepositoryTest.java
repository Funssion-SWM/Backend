package Funssion.Inforum.domain.interview.repository;

import Funssion.Inforum.domain.interview.constant.InterviewStatus;
import Funssion.Inforum.domain.interview.dto.InterviewAnswerDto;
import Funssion.Inforum.domain.interview.dto.QuestionsDto;
import Funssion.Inforum.domain.interview.service.InterviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class InterviewServiceTest {
    @Autowired
    InterviewService interviewService;
    @Autowired
    InterviewRepository interviewRepository;

    @Test
    @DisplayName("답변 제출 후 문제 풀이 상태 갱신 확인")
    void saveAnswerOfInterview(){
        Long employerId = 1L;
        Long employeeId = 2L;
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employerId, "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
        );

        interviewRepository.saveQuestions(employeeId,new QuestionsDto("1번 질문","2번 질문","3번 질문"));

        InterviewAnswerDto interviewAnswerDto = new InterviewAnswerDto(employerId, 1, "1번 답변입니다.");
        interviewService.startInterview(employerId,employeeId);
        assertThat(interviewService.saveAnswerOfQuestion(interviewAnswerDto,employeeId)).isEqualTo(InterviewStatus.ING_Q2);
    }

    @Test
    @DisplayName("2번답변 제출 후 문제 풀이 상태 갱신 확인")
    void saveAnswerOfInterviewAfterDoneQuestion2(){
        Long employerId = 1L;
        Long employeeId = 2L;
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employerId, "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
        );

        interviewRepository.saveQuestions(employeeId,new QuestionsDto("1번 질문","2번 질문","3번 질문"));

        interviewService.startInterview(employerId,employeeId);
        interviewService.saveAnswerOfQuestion(new InterviewAnswerDto(employerId,1,"1번 답변"),employeeId);
        assertThat( interviewService.saveAnswerOfQuestion(new InterviewAnswerDto(employerId, 2, "2번 답변입니다."),employeeId)).isEqualTo(InterviewStatus.ING_Q3);
    }

    @Test
    @DisplayName("현재 상태와 다른 문제의 답변을 제출한 경우")
    void saveAnswerOfInterviewMismatchStatus(){
        Long employerId = 1L;
        Long employeeId = 2L;
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employerId, "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
        );

        interviewRepository.saveQuestions(employeeId,new QuestionsDto("1번 질문","2번 질문","3번 질문"));

        assertThatThrownBy(()->interviewService.saveAnswerOfQuestion(new InterviewAnswerDto(employerId,2,"2번 답변"),employeeId)).hasMessage("잘못된 문제로 요청을 보내고 있습니다. 현 문제번호와 답변을 확인해주세요.");
    }
}