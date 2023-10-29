package Funssion.Inforum.domain.professionalprofile;

import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import Funssion.Inforum.domain.professionalprofile.dto.request.SaveProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.repository.ProfessionalProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ProfessionalProfileIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProfessionalProfileRepository professionalProfileRepository;

    String fullRequestForm =
            "{" +
                    "\"introduce\": \"안녕하세요\"," +
                    "\"techStack\": \"[{\\\"stack\\\": \\\"Spring\\\", \\\"level\\\": 5}]\"," +
                    "\"description\": \"스프링 장인\"," +
                    "\"answer1\": \"~~~ 였습니다.\"," +
                    "\"answer2\": \"앞으로는 ~~~\"," +
                    "\"answer3\": \"저는 ~~ 입니다.\"," +
                    "\"resume\": \"{\\\"content\\\": \\\"저는~~~\\\"}\"" +
                    "}";

    String updateRequestForm =
            "{" +
                    "\"introduce\": \"updated\"," +
                    "\"techStack\": \"[{\\\"stack\\\": \\\"updated\\\", \\\"level\\\": 5}]\"," +
                    "\"answer1\": \"updated\"," +
                    "\"answer2\": \"updated\"," +
                    "\"answer3\": \"updated\"," +
                    "\"resume\": \"updated\"" +
            "}";

    SaveMemberResponseDto createdMember1;
    SaveMemberResponseDto createdMember2;
    SaveMemberResponseDto createdMemberWithProfessionalProfile;
    SaveProfessionalProfileDto createdProfessionalProfileDto = SaveProfessionalProfileDto.builder()
            .introduce("hi")
            .techStack("[{\"stack\": \"java\", \"level\": 5}]")
            .description("java gosu")
            .answer1("yes")
            .answer2("no")
            .answer3("good")
            .resume("{\"content\": \"i'm a amazing programmer\"}")
            .build();;

    @BeforeEach
    void init() {
        createdMember1 = memberRepository.save(
                SocialMember.createSocialMember("test@gmail.com", "jinu")
        );
        createdMember2 = memberRepository.save(
                SocialMember.createSocialMember("test2@gmail.com", "jinu2")
        );
        createdMemberWithProfessionalProfile = memberRepository.save(
                SocialMember.createSocialMember("test3@gmail.com", "jinu3")
        );
        professionalProfileRepository.create(
                createdMemberWithProfessionalProfile.getId(),
                createdProfessionalProfileDto);

    }

    @Nested
    @DisplayName("자소서, 이력서 작성")
    class CreateProfessionalProfile {

        @Test
        @DisplayName("로그인 하지 않은 경우")
        void createWithoutLogin() throws Exception {
            mvc.perform(post("/professional-profile"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("정상 작성")
        void success() throws Exception {

            mvc.perform(post("/professional-profile")
                    .with(user(createdMember1.getId().toString()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(fullRequestForm))
                    .andExpect(status().isCreated());

            assertThat(professionalProfileRepository.findByUserId(createdMember1.getId()))
                    .isInstanceOf(ProfessionalProfile.class);

            mvc.perform(post("/professional-profile")
                            .with(user(createdMember2.getId().toString()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequestForm))
                    .andExpect(status().isCreated());

            assertThat(professionalProfileRepository.findByUserId(createdMember2.getId()))
                    .isInstanceOf(ProfessionalProfile.class);
        }
    }

    @Nested
    @DisplayName("자소서, 이력서 업데이트")
    class UpdatePersonalStatement {

        @Test
        @DisplayName("로그인 검증 실패 케이스")
        void validateEx() throws Exception {

            mvc.perform(put("/professional-profile")
                            .content(updateRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("생성하기 전 수정하는 케이스")
        void updateBeforeCreated() throws Exception {
            mvc.perform(put("/professional-profile")
                            .with(user(createdMember1.getId().toString()))
                            .content(updateRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("정상 수정 케이스")
        void updateSuccess() throws Exception {
            mvc.perform(put("/professional-profile")
                            .with(user(createdMemberWithProfessionalProfile.getId().toString()))
                            .content(updateRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            ProfessionalProfile updatedProfile = professionalProfileRepository.findByUserId(createdMemberWithProfessionalProfile.getId());
            assertThat(updatedProfile.getUserId()).isEqualTo(createdMemberWithProfessionalProfile.getId());
            assertThat(updatedProfile.getIntroduce()).isEqualTo("updated");
        }

    }

    @Nested
    @DisplayName("자소서, 이력서 조회하기")
    class GetProfessionalProfile {
    }


}
