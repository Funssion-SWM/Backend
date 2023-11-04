package Funssion.Inforum.domain.employer;

import Funssion.Inforum.common.constant.Role;
import Funssion.Inforum.domain.employer.repository.EmployerRepository;
import Funssion.Inforum.domain.interview.constant.InterviewStatus;
import Funssion.Inforum.domain.interview.dto.QuestionsDto;
import Funssion.Inforum.domain.interview.repository.InterviewRepository;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.EmployerSaveDto;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.member.service.AuthService;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import Funssion.Inforum.domain.professionalprofile.dto.request.SaveProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.repository.ProfessionalProfileRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EmployerIntegrationTestV2 {
    @Autowired
    MockMvc mvc;
    @Autowired
    AuthService authService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EmployerRepository employerRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    ProfessionalProfileRepository professionalProfileRepository;
    @Autowired
    InterviewRepository interviewRepository;

    SaveMemberResponseDto saveEmployeeDto;
    SaveMemberResponseDto saveEmployerDto;
    QuestionsDto questionsDto = new QuestionsDto("1번 질문", "2번 질문", "3번 질문");

    SaveProfessionalProfileDto createdProfessionalProfileDto = SaveProfessionalProfileDto.builder()
            .introduce("hi")
            .techStack("{\"java\": 5}")
            .answer1("yes")
            .answer2("no")
            .answer3("good")
            .resume("{\"content\": \"i'm a amazing programmer\"}")
            .build();
    @BeforeEach
    void setEmployerAndEmployee(){
        saveEmployeeDto = memberRepository.save(
                NonSocialMember.createNonSocialMember(
                        new MemberSaveDto("employee1", LoginType.NON_SOCIAL, "test1@gmail.com", "a1234567!"))
        );
        saveEmployerDto = memberRepository.save(EmployerSaveDto.builder()
                .userName("Mock(향로)")
                .loginType(LoginType.NON_SOCIAL)
                .userEmail("employer@gmail.com")
                .userPw("a1234567!")
                .companyName("inflearn")
                .build());

        professionalProfileRepository.create(
                saveEmployeeDto.getId(),
                createdProfessionalProfileDto);
    }

    @Test
    @DisplayName("인증받은 채용자가 지원자를 스크랩 한다.")
    void authorizedEmployerLikesEmployee() throws Exception {
        memberRepository.authorizeEmployer(saveEmployerDto.getId());
        UserDetails employerUserDetails = authService.loadUserByUsername(saveEmployerDto.getEmail());
        mvc.perform(post("/employer/like/"+saveEmployeeDto.getId())
                        .with(user(employerUserDetails)))
                .andExpect(status().isOk());
        assertThat(employerRepository.doesEmployerLikeEmployee(saveEmployerDto.getId(), saveEmployeeDto.getId())).isEqualTo(true);
        assertThat(notificationRepository.find30DaysNotificationsMaximum20ByUserId(saveEmployeeDto.getId())).hasSize(1);
    }

    @Test
    @DisplayName("인증받은 채용자가 지원자를 스크랩을 취소한다.")
    void authorizedEmployerCancelLikesEmployee() throws Exception {
        memberRepository.authorizeEmployer(saveEmployerDto.getId());
        UserDetails employerUserDetails = authService.loadUserByUsername(saveEmployerDto.getEmail());
        mvc.perform(post("/employer/like/"+saveEmployeeDto.getId())
                        .with(user(employerUserDetails)));

        mvc.perform(delete("/employer/like/"+saveEmployeeDto.getId())
                        .with(user(employerUserDetails)))
                .andExpect(status().isOk());
        assertThat(employerRepository.doesEmployerLikeEmployee(saveEmployerDto.getId(), saveEmployeeDto.getId())).isEqualTo(false);
    }

    @Test
    @DisplayName("임시 채용자가 지원자를 스크랩할 권한이 없다.")
    void unAuthorizedEmployerLikesEmployee() throws Exception {
        UserDetails employerUserDetails = authService.loadUserByUsername(saveEmployerDto.getEmail());
        mvc.perform(post("/employer/like/"+saveEmployeeDto.getId())
                        .with(user(employerUserDetails)))
                .andExpect(status().isForbidden());
        assertThat(employerUserDetails.getAuthorities().toString()).isEqualTo("["+Role.TEMP_EMPLOYER.getRoles()+"]");

    }

    @Test
    @DisplayName("채용자가 인터뷰를 완료한 유저 리스트를 받아봅니다.")
    void getInterviewUserList() throws Exception{
        SaveProfessionalProfileDto saveProfessionalProfileDto = SaveProfessionalProfileDto.builder()
                .introduce("hi")
                .developmentArea("BACKEND")
                .techStack("[{\"stack\": \"java\", \"level\": 5}]")
                .answer1("yes")
                .answer2("no")
                .answer3("good")
                .resume("{\"content\": \"i'm a amazing programmer\"}")
                .build();
        memberRepository.authorizeEmployer(saveEmployerDto.getId());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(saveEmployerDto.getId(), "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
        );
        SaveMemberResponseDto saveEmployeeDto = memberRepository.save(
                NonSocialMember.createNonSocialMember(
                        new MemberSaveDto("employee1", LoginType.NON_SOCIAL, "test1@gmail.com", "a1234567!"))
        );
        SaveMemberResponseDto saveEmployeeDto2 = memberRepository.save(
                NonSocialMember.createNonSocialMember(
                        new MemberSaveDto("employee2", LoginType.NON_SOCIAL, "test2@gmail.com", "a1234567!"))
        );
        SaveMemberResponseDto saveEmployeeDto3 = memberRepository.save(
                NonSocialMember.createNonSocialMember(
                        new MemberSaveDto("employee3", LoginType.NON_SOCIAL, "test3@gmail.com", "a1234567!"))
        );
        professionalProfileRepository.create(
                saveEmployeeDto.getId(),
                saveProfessionalProfileDto
        );
        professionalProfileRepository.create(
                saveEmployeeDto2.getId(),
                saveProfessionalProfileDto
        );
        professionalProfileRepository.create(
                saveEmployeeDto3.getId(),
                saveProfessionalProfileDto
        );
        interviewRepository.saveQuestions(saveEmployeeDto.getId(),questionsDto);
        interviewRepository.saveQuestions(saveEmployeeDto2.getId(),questionsDto);
        interviewRepository.saveQuestions(saveEmployeeDto3.getId(),questionsDto);

        interviewRepository.updateStatus(saveEmployerDto.getId(),saveEmployeeDto2.getId(), InterviewStatus.DONE);
        UserDetails employerUserDetails = authService.loadUserByUsername(saveEmployerDto.getEmail());

        MvcResult result = mvc.perform(get("/employer/employees")
                        .with(user(employerUserDetails))
                        .param("done", "true"))
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        List<String> employeesTechStack = JsonPath.read(contentAsString, "$[*].techStack");
        assertThat(employeesTechStack).hasSize(1);

    }

    @Test
    @DisplayName("면접이 완료되지 않은 지원자의 면접 결과를 가져올 수 없다.")
    void cannotAccessResultWhenEmployeeNotDone() throws Exception {
        memberRepository.authorizeEmployer(saveEmployerDto.getId());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(saveEmployerDto.getId(), "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
        );
        interviewRepository.saveQuestions(saveEmployeeDto.getId(),questionsDto);
        UserDetails employerUserDetails = authService.loadUserByUsername(saveEmployerDto.getEmail());
        mvc.perform(get("/employer/interview-result/"+saveEmployeeDto.getId())
                        .with(user(employerUserDetails)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("면접이 완료된 사람의 면접 결과를 가져올 수 있다..")
    void canOnlyAccessResultWhenEmployeeDone() throws Exception {
        memberRepository.authorizeEmployer(saveEmployerDto.getId());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(saveEmployerDto.getId(), "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
        );
        interviewRepository.saveQuestions(saveEmployeeDto.getId(),questionsDto);
        interviewRepository.updateStatus(saveEmployerDto.getId(),saveEmployeeDto.getId(), InterviewStatus.DONE);

        UserDetails employerUserDetails = authService.loadUserByUsername(saveEmployerDto.getEmail());
        mvc.perform(get("/employer/interview-result/"+saveEmployeeDto.getId())
                        .with(user(employerUserDetails)))
                .andExpect(status().isOk());

    }
    @Test
    @WithMockUser(roles="USER")
    @DisplayName("일반 유저는 EMPLOYER 권한의 api 요청을 할 수 없다.")
    void userCannotAccessEmployerLogic() throws Exception {
        mvc.perform(get("/employer/employees"))
                .andExpect(status().isForbidden());
    }

    private void saveUsersAndResumes(int numberOfUsers){
        for (int i = 0; i< numberOfUsers; i++) {
            saveEmployeeDto = memberRepository.save(
                    NonSocialMember.createNonSocialMember(
                            new MemberSaveDto("employee" + i, LoginType.NON_SOCIAL, "test"+numberOfUsers+"@gmail.com", "a1234567!"))
            );
            professionalProfileRepository.create(
                    saveEmployeeDto.getId(),
                    createdProfessionalProfileDto);
        }
    }
}