package Funssion.Inforum.domain.professionalprofile;

import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import Funssion.Inforum.domain.professionalprofile.dto.request.CreateProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.repository.ProfessionalProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
                    "\"techStack\": \"{\\\"Spring\\\": 5}\"," +
                    "\"description\": \"스프링 장인\"," +
                    "\"answer1\": \"~~~ 였습니다.\"," +
                    "\"answer2\": \"앞으로는 ~~~\"," +
                    "\"answer3\": \"저는 ~~ 입니다.\"," +
                    "\"resume\": \"{\\\"content\\\": \\\"저는~~~\\\"}\"" +
                    "}";

    String minimumRequestForm =
            "{" +
                    "\"introduce\": \"안녕하세요\"," +
                    "\"techStack\": \"{\\\"Spring\\\": 5}\"," +
                    "\"answer1\": \"~~~ 였습니다.\"," +
                    "\"answer2\": \"앞으로는 ~~~\"," +
                    "\"answer3\": \"저는 ~~ 입니다.\"" +
                    "}";

    String noIntroduceRequestForm = "{" +
            "\"introduce\": \"\"," +
            "\"techStack\": \"{\\\"Spring\\\": 5}\"," +
            "\"answer1\": \"~~~ 였습니다.\"," +
            "\"answer2\": \"앞으로는 ~~~\"," +
            "\"answer3\": \"저는 ~~ 입니다.\"" +
            "}";

    String noTechStackRequestForm = "{" +
            "\"introduce\": \"안녕하세요\"," +
            "\"techStack\": \"\"," +
            "\"answer1\": \"~~~ 였습니다.\"," +
            "\"answer2\": \"앞으로는 ~~~\"," +
            "\"answer3\": \"저는 ~~ 입니다.\"" +
            "}";

    String noAnswer1RequestForm = "{" +
            "\"introduce\": \"안녕하세요\"," +
            "\"techStack\": \"{\\\"Spring\\\": 5}\"," +
            "\"answer1\": \"\"," +
            "\"answer2\": \"앞으로는 ~~~\"," +
            "\"answer3\": \"저는 ~~ 입니다.\"" +
            "}";

    String noAnswer2RequestForm = "{" +
            "\"introduce\": \"안녕하세요\"," +
            "\"techStack\": \"{\\\"Spring\\\": 5}\"," +
            "\"answer1\": \"~~~ 였습니다.\"," +
            "\"answer2\": \"\"," +
            "\"answer3\": \"저는 ~~ 입니다.\"" +
            "}";

    String noAnswer3RequestForm = "{" +
            "\"introduce\": \"안녕하세요\"," +
            "\"techStack\": \"{\\\"Spring\\\": 5}\"," +
            "\"answer1\": \"~~~ 였습니다.\"," +
            "\"answer2\": \"앞으로는 ~~~\"," +
            "\"answer3\": \"\"" +
            "}";

    String rightResumeRequestForm = "{" +
            "\"resume\": \"{\\\"content\\\": \\\"저는~~~\\\"}\"" +
            "}";

    String emptyResumeRequestForm = "{" +
            "\"resume\": \"\"" +
            "}";

    String nullResumeRequestForm = "{" +
            "\"resume\": null" +
            "}";

    SaveMemberResponseDto createdMember1;
    SaveMemberResponseDto createdMember2;
    SaveMemberResponseDto createdMemberWithProfessionalProfile;
    CreateProfessionalProfileDto createdProfessionalProfileDto = CreateProfessionalProfileDto.builder()
            .introduce("hi")
            .techStack("{\"java\": 5}")
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
                            .content(minimumRequestForm))
                    .andExpect(status().isCreated());

            assertThat(professionalProfileRepository.findByUserId(createdMember2.getId()))
                    .isInstanceOf(ProfessionalProfile.class);
        }

        @Test
        @DisplayName("입력 값 검증 실패 케이스")
        void validateEx() throws Exception {
            mvc.perform(post("/professional-profile")
                    .with(user(createdMember1.getId().toString()))
                    .content(noIntroduceRequestForm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"한줄 자기소개는 필수 입력 사항입니다.\"")));

            mvc.perform(post("/professional-profile")
                            .with(user(createdMember1.getId().toString()))
                            .content(noTechStackRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"기술 스택은 필수 입력 사항입니다.\"")));

            mvc.perform(post("/professional-profile")
                            .with(user(createdMember1.getId().toString()))
                            .content(noAnswer1RequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"질문 1에 대한 답변을 작성해주세요.\"")));

            mvc.perform(post("/professional-profile")
                            .with(user(createdMember1.getId().toString()))
                            .content(noAnswer2RequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"질문 2에 대한 답변을 작성해주세요.\"")));

            mvc.perform(post("/professional-profile")
                            .with(user(createdMember1.getId().toString()))
                            .content(noAnswer3RequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"질문 3에 대한 답변을 작성해주세요.\"")));
        }
    }

    @Nested
    @DisplayName("자소서 업데이트")
    class UpdatePersonalStatement {

        @Test
        @DisplayName("입력 값, 로그인 검증 실패 케이스")
        void validateEx() throws Exception {

            mvc.perform(put("/professional-profile/personal-statement")
                            .content(minimumRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            mvc.perform(put("/professional-profile/personal-statement")
                            .with(user(createdMemberWithProfessionalProfile.getId().toString()))
                            .content(noIntroduceRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"한줄 자기소개는 필수 입력 사항입니다.\"")));

            mvc.perform(put("/professional-profile/personal-statement")
                            .with(user(createdMemberWithProfessionalProfile.getId().toString()))
                            .content(noTechStackRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"기술 스택은 필수 입력 사항입니다.\"")));

            mvc.perform(put("/professional-profile/personal-statement")
                            .with(user(createdMemberWithProfessionalProfile.getId().toString()))
                            .content(noAnswer1RequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"질문 1에 대한 답변을 작성해주세요.\"")));

            mvc.perform(put("/professional-profile/personal-statement")
                            .with(user(createdMemberWithProfessionalProfile.getId().toString()))
                            .content(noAnswer2RequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"질문 2에 대한 답변을 작성해주세요.\"")));

            mvc.perform(put("/professional-profile/personal-statement")
                            .with(user(createdMemberWithProfessionalProfile.getId().toString()))
                            .content(noAnswer3RequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"질문 3에 대한 답변을 작성해주세요.\"")));
        }

        @Test
        @DisplayName("생성하기 전 수정하는 케이스")
        void updateBeforeCreated() throws Exception {
            mvc.perform(put("/professional-profile/personal-statement")
                            .with(user(createdMember1.getId().toString()))
                            .content(minimumRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("정상 수정 케이스")
        void updateSuccess() throws Exception {
            mvc.perform(put("/professional-profile/personal-statement")
                            .with(user(createdMemberWithProfessionalProfile.getId().toString()))
                            .content(minimumRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            ProfessionalProfile updatedProfile = professionalProfileRepository.findByUserId(createdMemberWithProfessionalProfile.getId());
            assertThat(updatedProfile.getUserId()).isEqualTo(createdMemberWithProfessionalProfile.getId());
            assertThat(updatedProfile.getIntroduce()).isEqualTo("안녕하세요");
        }

    }

    @Nested
    @DisplayName("이력서 업데이트")
    class UpdateResume {

        @Test
        @DisplayName("로그인 검증 실패 케이스")
        void validateEx() throws Exception {

            mvc.perform(put("/professional-profile/resume")
                            .content(rightResumeRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("생성하기 전 수정하는 케이스")
        void updateBeforeCreated() throws Exception {
            mvc.perform(put("/professional-profile/resume")
                            .with(user(createdMember1.getId().toString()))
                            .content(rightResumeRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("정상 수정 케이스")
        void updateSuccess() throws Exception {
            mvc.perform(put("/professional-profile/resume")
                            .with(user(createdMemberWithProfessionalProfile.getId().toString()))
                            .content(rightResumeRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            ProfessionalProfile updatedProfile = professionalProfileRepository.findByUserId(createdMemberWithProfessionalProfile.getId());
            assertThat(updatedProfile.getUserId()).isEqualTo(createdMemberWithProfessionalProfile.getId());
            assertThat(updatedProfile.getResume()).isEqualTo("{\"content\": \"저는~~~\"}");

            mvc.perform(put("/professional-profile/resume")
                            .with(user(createdMemberWithProfessionalProfile.getId().toString()))
                            .content(emptyResumeRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            ProfessionalProfile updatedToEmptyResumeProfile = professionalProfileRepository.findByUserId(createdMemberWithProfessionalProfile.getId());
            assertThat(updatedToEmptyResumeProfile.getUserId()).isEqualTo(createdMemberWithProfessionalProfile.getId());
            assertThat(updatedToEmptyResumeProfile.getResume()).isEqualTo("");

            mvc.perform(put("/professional-profile/resume")
                            .with(user(createdMemberWithProfessionalProfile.getId().toString()))
                            .content(nullResumeRequestForm)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            ProfessionalProfile updatedToNullResumeProfile = professionalProfileRepository.findByUserId(createdMemberWithProfessionalProfile.getId());
            assertThat(updatedToNullResumeProfile.getUserId()).isEqualTo(createdMemberWithProfessionalProfile.getId());
            assertThat(updatedToNullResumeProfile.getResume()).isNull();
        }

    }

    @Nested
    @DisplayName("자소서, 이력서 조회하기")
    class GetProfessionalProfile {
    }


}
