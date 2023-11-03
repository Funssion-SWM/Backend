package Funssion.Inforum.domain.employer.repository;

import Funssion.Inforum.domain.employer.domain.Employee;
import Funssion.Inforum.domain.employer.domain.EmployeeWithStatus;
import Funssion.Inforum.domain.interview.constant.InterviewStatus;
import Funssion.Inforum.domain.interview.dto.QuestionsDto;
import Funssion.Inforum.domain.interview.repository.InterviewRepository;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.EmployerSaveDto;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.professionalprofile.dto.request.SaveProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.repository.ProfessionalProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EmployerRepositoryTest {
    @Autowired
    EmployerRepository employerRepository;
    @Autowired
    InterviewRepository interviewRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProfessionalProfileRepository profileRepository;
    SaveProfessionalProfileDto saveProfessionalProfileDto = SaveProfessionalProfileDto.builder()
            .introduce("hi")
            .developmentArea("BACKEND")
            .techStack("[{\"stack\": \"java\", \"level\": 5}]")
            .answer1("yes")
            .answer2("no")
            .answer3("good")
            .resume("{\"content\": \"i'm a amazing programmer\"}")
            .build();;

    Long initEmployeeId_1;
    Long initEmployeeId_2;
    Long initEmployeeId_3;
    Long initEmployerId;
    @BeforeEach
    void setEmployerAndEmployee(){
        SaveMemberResponseDto saveEmployeeDto = memberRepository.save(
                NonSocialMember.createNonSocialMember(
                        new MemberSaveDto("employee1", LoginType.NON_SOCIAL, "test1@gmail.com", "a1234567!"))
        );
        initEmployeeId_1 = saveEmployeeDto.getId();
        SaveMemberResponseDto saveEmployeeDto2 = memberRepository.save(
                NonSocialMember.createNonSocialMember(
                        new MemberSaveDto("employee2", LoginType.NON_SOCIAL, "test2@gmail.com", "a1234567!"))
        );
        initEmployeeId_2 = saveEmployeeDto2.getId();
        SaveMemberResponseDto saveEmployeeDto3 = memberRepository.save(
                NonSocialMember.createNonSocialMember(
                        new MemberSaveDto("employee3", LoginType.NON_SOCIAL, "test3@gmail.com", "a1234567!"))
        );
        initEmployeeId_3 = saveEmployeeDto3.getId();
        profileRepository.create(
                saveEmployeeDto.getId(),
                saveProfessionalProfileDto
        );
        profileRepository.create(
                saveEmployeeDto2.getId(),
                saveProfessionalProfileDto
        );
        profileRepository.create(
                saveEmployeeDto3.getId(),
                saveProfessionalProfileDto
        );

        SaveMemberResponseDto saveEmployerDto = memberRepository.save(EmployerSaveDto.builder()
                .userName("Mock(향로)")
                .loginType(LoginType.NON_SOCIAL)
                .userEmail("employer@gmail.com")
                .userPw("a1234567!")
                .companyName("inflearn")
                .build());
        initEmployerId = saveEmployerDto.getId();
    }

    @Nested
    @DisplayName("면접자 리스트 받아오기")
    class getInterviewEmployees{
        QuestionsDto questionsDto = new QuestionsDto("1번 질문", "2번 질문", "3번 질문");
        @Test
        @DisplayName("면접 진행중인 면접자들 받아오기")
        void getInterviewingEmployeeList(){
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(initEmployerId, "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
            );
            interviewRepository.saveQuestions(initEmployeeId_1,questionsDto);
            interviewRepository.saveQuestions(initEmployeeId_2,questionsDto);
            interviewRepository.saveQuestions(initEmployeeId_3,questionsDto);

            assertThat(interviewRepository.getInterviewQuestionOf(initEmployeeId_1,initEmployerId).getStatus()).isEqualTo(InterviewStatus.READY.toString());

            List<Employee> notDoneInterviewEmployees = employerRepository.getInterviewEmployees(false);
            assertThat(notDoneInterviewEmployees).hasSize(3);
        }
        @Test
        @DisplayName("면접 완료된 면접자들 받아오기")
        void getFinishedIntervewEmployeeList(){
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(initEmployerId, "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
            );
            interviewRepository.saveQuestions(initEmployeeId_1,questionsDto);
            interviewRepository.saveQuestions(initEmployeeId_2,questionsDto);
            interviewRepository.saveQuestions(initEmployeeId_3,questionsDto);

            interviewRepository.updateStatus(initEmployerId, initEmployeeId_1,InterviewStatus.DONE);
            interviewRepository.updateStatus(initEmployerId, initEmployeeId_2,InterviewStatus.DONE);

            List<Employee> notDoneInterviewEmployees = employerRepository.getInterviewEmployees(true);
            assertThat(notDoneInterviewEmployees).hasSize(2);
        }

        @Test
        @DisplayName("employer가 좋아요한 유저들 리스트 가져오기")
        void getLikeEmployeesByEmployer(){
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(initEmployerId, "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
            );
            employerRepository.likeEmployee(initEmployeeId_1);
            employerRepository.likeEmployee(initEmployeeId_2);

            List<EmployeeWithStatus> likeEmployees = employerRepository.getLikeEmployees();
            assertThat(likeEmployees).hasSize(2);
            assertThat(likeEmployees.get(0).getStatus()).isNull();
        }

        @Test
        @DisplayName("employer가 좋아요한 유저들 리스트 가져오는데, 인터뷰 상태도 확인")
        void getLikeEmployeesWithStatusByEmployer(){
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(initEmployerId, "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
            );
            employerRepository.likeEmployee(initEmployeeId_1);
            employerRepository.likeEmployee(initEmployeeId_2);

            interviewRepository.saveQuestions(initEmployeeId_1,questionsDto);

            List<EmployeeWithStatus> likeEmployees = employerRepository.getLikeEmployees();
            assertThat(likeEmployees).hasSize(2);
            assertThat(likeEmployees.stream().map((employee)->employee.getStatus())).contains(null,InterviewStatus.READY);
        }
    }
}