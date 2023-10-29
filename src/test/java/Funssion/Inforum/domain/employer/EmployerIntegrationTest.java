package Funssion.Inforum.domain.employer;

import Funssion.Inforum.domain.employer.repository.EmployerRepository;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.request.EmployerSaveDto;
import Funssion.Inforum.domain.member.dto.request.MemberSaveDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EmployerIntegrationTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EmployerRepository employerRepository;

    static Long initEmployeeId;
    static Long initEmployerId;
    @BeforeEach
    void setEmployerAndEmployee(){
        SaveMemberResponseDto saveEmployeeDto = memberRepository.save(
                NonSocialMember.createNonSocialMember(
                        new MemberSaveDto("employee1", LoginType.NON_SOCIAL, "test1@gmail.com", "a1234567!"))
        );
        initEmployeeId = saveEmployeeDto.getId();
        SaveMemberResponseDto saveEmployerDto = memberRepository.save(EmployerSaveDto.builder()
                .userName("Mock(향로)")
                .loginType(LoginType.NON_SOCIAL)
                .userEmail("employer@gmail.com")
                .userPw("a1234567!")
                .companyName("inflearn")
                .build());
        initEmployerId = saveEmployerDto.getId();
    }

    @Test
    @WithMockUser(roles="EMPLOYER")
    @DisplayName("채용자가 지원자를 스크랩 한다.")
    void employerLikesEmployee() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(initEmployerId, "a1234567!", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYER")))
        );
        mvc.perform(post("/employer/like/"+initEmployeeId))
                .andExpect(status().isOk());
        assertThat(employerRepository.doesEmployerLikeEmployee(initEmployerId, initEmployeeId)).isEqualTo(true);
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("일반 유저는 EMPLOYER 권한 api에 금지된다.")
    void userCannotAccessEmployerLogic() throws Exception {
        mvc.perform(get("/employer/employees"))
                .andExpect(status().isForbidden());
    }

}
