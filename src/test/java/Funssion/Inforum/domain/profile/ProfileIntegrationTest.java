package Funssion.Inforum.domain.profile;

import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.CustomUserDetails;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.professionalprofile.dto.request.SaveProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.repository.ProfessionalProfileRepository;
import Funssion.Inforum.domain.profile.repository.ProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static Funssion.Inforum.common.constant.Role.EMPLOYER;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ProfileIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    ProfessionalProfileRepository professionalProfileRepository;

    ObjectMapper mapper = new ObjectMapper();

    SaveMemberResponseDto createdMember1;
    SaveMemberResponseDto createdMember2;
    SaveMemberResponseDto createdMember3;

    @BeforeEach
    void init() {
        createdMember1 = memberRepository.save(SocialMember.createSocialMember("jinu1@gmail.com", "jinu1"));
        createdMember2 = memberRepository.save(SocialMember.createSocialMember("jinu2@gmail.com", "jinu2"));
        createdMember3 = memberRepository.save(SocialMember.createSocialMember("jinu3@gmail.com", "jinu3"));

        professionalProfileRepository.create(createdMember1.getId(), SaveProfessionalProfileDto.builder()
                .introduce("test")
                .developmentArea("Backend")
                .techStack("[{\"stack\":\"Java\", \"level\":5}, {\"stack\":\"Python\", \"level\":5}, {\"stack\":\"React\", \"level\":5}]")
                .answer1("test")
                .answer2("test")
                .answer3("test")
                .resume("{\"content\": \"i'm a amazing programmer\"}")
                .description("test")
                .build());

        professionalProfileRepository.create(createdMember2.getId(), SaveProfessionalProfileDto.builder()
                .introduce("test")
                .developmentArea("Frontend")
                .techStack("[{\"stack\":\"Java\", \"level\":5}, {\"stack\":\"Python\", \"level\":5}, {\"stack\":\"React\", \"level\":5}]")
                .answer1("test")
                .answer2("test")
                .answer3("test")
                .resume("{\"content\": \"i'm a amazing programmer\"}")
                .description("test")
                .build());

        professionalProfileRepository.create(createdMember3.getId(), SaveProfessionalProfileDto.builder()
                .introduce("test")
                .developmentArea("Backend")
                .techStack("[{\"stack\":\"Java\", \"level\":5}, {\"stack\":\"Python\", \"level\":5}, {\"stack\":\"Go\", \"level\":5}]")
                .answer1("test")
                .answer2("test")
                .answer3("test")
                .resume("{\"content\": \"i'm a amazing programmer\"}")
                .description("test")
                .build());
    }

    @Nested
    @DisplayName("유저 프로필 맞춤 조회")
    class searchUserProfiles {

        UserDetails mockEmployerUserDetails;
        TechStackDto techStackDto = TechStackDto.builder()
                .developmentArea("Backend")
                .techStacks(List.of("Java","Python", "Go"))
                .build();
        String requestTechStackForm;

        @BeforeEach
        void init() throws JsonProcessingException {
            mockEmployerUserDetails = new CustomUserDetails(createdMember2.getId(), EMPLOYER.getRoles(), createdMember2.getName(),"test",true,false);
            requestTechStackForm = mapper.writeValueAsString(techStackDto);
            System.out.println(requestTechStackForm);
        }

        @Test
        @DisplayName("로그인, 채용자 인지 검증")
        void validateAuth() throws Exception {
            mvc.perform(get("/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestTechStackForm))
                    .andExpect(status().isUnauthorized());

            mvc.perform(get("/users/profile")
                    .with(user(createdMember1.getId().toString()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestTechStackForm))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("정상 조회")
        void success() throws Exception {
            mvc.perform(get("/users/profile")
                            .with(user(mockEmployerUserDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestTechStackForm))
                    .andExpect(content().string(containsString("\"id\":" + createdMember1.getId())))
                    .andExpect(content().string(not(containsString("\"id\":" + createdMember2.getId()))))
                    .andExpect(content().string(containsString("\"id\":" + createdMember3.getId())));
        }
    }
}
