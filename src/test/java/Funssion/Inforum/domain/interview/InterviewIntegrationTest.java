package Funssion.Inforum.domain.interview;

import Funssion.Inforum.domain.employer.repository.EmployerRepository;
import Funssion.Inforum.domain.interview.constant.InterviewStatus;
import Funssion.Inforum.domain.interview.domain.Interview;
import Funssion.Inforum.domain.interview.dto.InterviewAnswerDto;
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
import Funssion.Inforum.domain.professionalprofile.dto.request.CreateProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.repository.ProfessionalProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InterviewIntegrationTest {
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
    InterviewRepository interviewRepository;
    @Autowired
    ProfessionalProfileRepository professionalProfileRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    SaveMemberResponseDto saveEmployeeDto;
    SaveMemberResponseDto saveEmployerDto;

    CreateProfessionalProfileDto createdProfessionalProfileDto = CreateProfessionalProfileDto.builder()
            .introduce("hi")
            .techStack("{\"java\": 5}")
            .description("java gosu")
            .answer1("yes")
            .answer2("no")
            .answer3("good")
            .resume("{\"content\": \"i'm a amazing programmer\"}")
            .build();
    QuestionsDto questionsDto = new QuestionsDto("1번 질문","2번 질문", "3번 질문");



    @BeforeEach
    void init() {
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

    @Nested
    @DisplayName("면접 요청")
    class requestInterview{
        @Test
        @DisplayName("지원자에게 질문과 함께 면접을 요청합니다.")
        void requestInterviewWithQuestions() throws Exception {
            memberRepository.authorizeEmployer(saveEmployerDto.getId());
            UserDetails authorizedEmployerUserDetails = authService.loadUserByUsername(saveEmployerDto.getEmail());


            mvc.perform(post("/interview/questions/" + saveEmployeeDto.getId())
                    .with(user(authorizedEmployerUserDetails))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(questionsDto)));
            Interview interviewQuestionOf = interviewRepository.getInterviewQuestionOf(saveEmployeeDto.getId(), saveEmployerDto.getId());
            assertThat(interviewQuestionOf.equals(new Interview(saveEmployerDto.getId(), InterviewStatus.READY.toString(), questionsDto.getQuestion1(), questionsDto.getQuestion2(), questionsDto.getQuestion3())));
        }

        @Test
        @DisplayName("지원자에게 면접요청을 하고나서 중복해서 요청을 보낼 수 없습니다.")
        void requestInterviewAgainBlocked() throws Exception {
            memberRepository.authorizeEmployer(saveEmployerDto.getId());
            UserDetails authorizedEmployerUserDetails = authService.loadUserByUsername(saveEmployerDto.getEmail());
            mvc.perform(post("/interview/questions/" + saveEmployeeDto.getId())
                    .with(user(authorizedEmployerUserDetails))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(questionsDto)));

            MvcResult result = mvc.perform(post("/interview/questions/" + saveEmployeeDto.getId())
                            .with(user(authorizedEmployerUserDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(questionsDto)))
                    .andExpect(status().isConflict())
                    .andReturn();
            assertThat(result.getResolvedException().getMessage().equals("이미 면접요청을 보낸 지원자입니다."));
        }

        @Test
        @DisplayName("일반 유저가 면접을 요청할 수 없습니다.")
        void requestInterviewForbiddenByUser() throws Exception {
            UserDetails employee = authService.loadUserByUsername(saveEmployeeDto.getEmail());
            mvc.perform(post("/interview/questions/" + saveEmployeeDto.getId())
                            .with(user(employee))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(questionsDto)))
                    .andExpect(status().isForbidden());
        }
        @Test
        @DisplayName("인증받지 않은 채용자가 면접을 요청할 수 없습니다.")
        void requestInterviewForbiddenByUnauthorizedEmployer() throws Exception {
            UserDetails unAuthorizedEmployee = authService.loadUserByUsername(saveEmployerDto.getEmail());
            mvc.perform(post("/interview/questions/" + saveEmployeeDto.getId())
                            .with(user(unAuthorizedEmployee))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(questionsDto)))
                    .andExpect(status().isForbidden());
        }
    }
    @Nested
    @DisplayName("면접 질문들 가져오기")
    class getQuestions {
        @Test
        @DisplayName("면접 질문을 저장하고 가져오기")
        void getQuestions() throws Exception {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(saveEmployerDto.getId(), "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
            );
            interviewRepository.saveQuestions(saveEmployeeDto.getId(), questionsDto);

            UserDetails employee = authService.loadUserByUsername(saveEmployeeDto.getEmail());
            MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/interview/questions/" + saveEmployerDto.getId() + "/" + saveEmployeeDto.getId())
                            .with(user(employee)))
                    .andReturn();
            String responseBody = result.getResponse().getContentAsString();
            String companyNameOfEmployer = JsonPath.read(responseBody, "$.companyName");
            String question1OfEmployer = JsonPath.read(responseBody, "$.question1");
            String question2OfEmployer = JsonPath.read(responseBody, "$.question2");
            String question3OfEmployer = JsonPath.read(responseBody, "$.question3");
            assertThat(companyNameOfEmployer.equals(saveEmployerDto.getCompanyName()));
            assertThat(question1OfEmployer.equals(questionsDto.getQuestion1()));
            assertThat(question2OfEmployer.equals(questionsDto.getQuestion2()));
            assertThat(question3OfEmployer.equals(questionsDto.getQuestion3()));
        }
    }
    @Nested
    @DisplayName("답변 제출")
    class answerInterview{
        @Test
        @DisplayName("면접을 시작 후 바로 나갔을 경우에 (1번문제 봄) status 확인")
        void getQuestionsWhenDisconnected() throws Exception {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(saveEmployerDto.getId(), "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
            );
            interviewRepository.saveQuestions(saveEmployeeDto.getId(),questionsDto);
            UserDetails employee = authService.loadUserByUsername(saveEmployeeDto.getEmail());
            mvc.perform(put("/interview/start/" + saveEmployerDto.getId())
                            .with(user(employee)));
            assertThat(interviewRepository.getInterviewStatusOfUser(saveEmployerDto.getId(), saveEmployeeDto.getId())).isEqualTo(InterviewStatus.ING_Q1);
        }

        @Test
        @DisplayName("1번문제를 보는중 나가졌을 경우 1번문제의 답은 제출할 수 없다.")
        void getQuestionsWhenDisconnectedInFirstQuestion() throws Exception {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(saveEmployerDto.getId(), "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
            );
            interviewRepository.saveQuestions(saveEmployeeDto.getId(),questionsDto);
            UserDetails employee = authService.loadUserByUsername(saveEmployeeDto.getEmail());
            mvc.perform(put("/interview/start/" + saveEmployerDto.getId())
                    .with(user(employee)));

            InterviewAnswerDto interviewAnswerDto = new InterviewAnswerDto(saveEmployerDto.getId(), 1,"1번문제의 답입니다.");
            String firstAnswerString = objectMapper.writeValueAsString(interviewAnswerDto);
            MvcResult result = mvc.perform(post("/interview/answers")
                            .with(user(employee))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(firstAnswerString))
                    .andExpect(status().isBadRequest())
                    .andReturn();
            assertThat(result.getResolvedException().getMessage()).isEqualTo("잘못된 문제로 요청을 보내고 있습니다. 현 문제번호와 답변을 확인해주세요.");
        }
    }

}