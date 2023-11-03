package Funssion.Inforum.domain.employer;

import Funssion.Inforum.common.constant.Role;
import Funssion.Inforum.domain.employer.repository.EmployerRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

    SaveMemberResponseDto saveEmployeeDto;
    SaveMemberResponseDto saveEmployerDto;

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

//    @Test
//    @DisplayName("권한받은 채용자가 지원자 리스트를 받아본다.")
//    void getEmployeesLIst() throws Exception {
//        memberRepository.authorizeEmployer(saveEmployerDto.getId());
//        saveUsersAndResumes(7); //기존 저장 1명 + 7 명 = 총 8명
//        UserDetails employerUserDetails = authService.loadUserByUsername(saveEmployerDto.getEmail());
//        MvcResult firstResult = mvc.perform(get("/employer/employees")
//                        .with(user(employerUserDetails)))
//                .andReturn();
//        String contentAsString1 = firstResult.getResponse().getContentAsString();
//        List<EmployeeDto> employees1 = JsonPath.read(contentAsString1, "$");
//        assertThat(employees1).hasSize(5);
//
//        MvcResult secondResult = mvc.perform(get("/employer/employees")
//                        .param("page","1")
//                        .with(user(employerUserDetails)))
//                .andReturn();
//        String contentAsString2 = secondResult.getResponse().getContentAsString();
//        List<EmployeeDto> employees2 = JsonPath.read(contentAsString2, "$");
//        assertThat(employees2).hasSize(3);
//
//        MvcResult thirdResult = mvc.perform(get("/employer/employees")
//                        .param("page","2")
//                        .with(user(employerUserDetails)))
//                .andReturn();
//        String contentAsString3 = thirdResult.getResponse().getContentAsString();
//        List<EmployeeDto> employees3 = JsonPath.read(contentAsString3, "$");
//        assertThat(employees3).hasSize(0);
//    }
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