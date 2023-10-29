package Funssion.Inforum.domain.professionalprofile.repository;

import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import Funssion.Inforum.domain.professionalprofile.dto.request.SaveProfessionalProfileDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ProfessionalProfileRepositoryImplTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProfessionalProfileRepository profileRepository;

    SaveMemberResponseDto createdMember;
    ProfessionalProfile savedProfile;
    SaveProfessionalProfileDto saveProfessionalProfileDto = SaveProfessionalProfileDto.builder()
            .introduce("hi")
            .developmentArea("BACKEND")
            .techStack("[{\"stack\": \"java\", \"level\": 5}]")
            .description("java gosu")
            .answer1("yes")
            .answer2("no")
            .answer3("good")
            .resume("{\"content\": \"i'm a amazing programmer\"}")
            .build();;

    @BeforeEach
    void init() {
        createdMember = memberRepository.save(
                SocialMember.createSocialMember("test@gmail.com", "jinu")
        );

        profileRepository.create(
                createdMember.getId(),
                saveProfessionalProfileDto
        );

        savedProfile = profileRepository.findByUserId(createdMember.getId());
    }

    @Test
    @DisplayName("부가 프로필 수정하기")
    void updatePersonalStatement() {
        SaveProfessionalProfileDto updatePersonalStatementDto = SaveProfessionalProfileDto.builder()
                .introduce("updated")
                .developmentArea("updated")
                .techStack("[{\"level\": 5, \"stack\": \"updated\"}]")
                .description("updated")
                .answer1("updated")
                .answer2("updated")
                .answer3("updated")
                .resume("updated")
                .build();

        profileRepository.update(savedProfile.getUserId(), updatePersonalStatementDto);

        ProfessionalProfile updatedProfile = profileRepository.findByUserId(savedProfile.getUserId());

        assertThat(updatedProfile.getIntroduce()).isEqualTo(updatePersonalStatementDto.getIntroduce());
        assertThat(updatedProfile.getDevelopmentArea()).isEqualTo(updatePersonalStatementDto.getDevelopmentArea());
        assertThat(updatedProfile.getTechStack()).isEqualTo(updatePersonalStatementDto.getTechStack());
        assertThat(updatedProfile.getDescription()).isEqualTo(updatePersonalStatementDto.getDescription());
        assertThat(updatedProfile.getAnswer1()).isEqualTo(updatePersonalStatementDto.getAnswer1());
        assertThat(updatedProfile.getAnswer2()).isEqualTo(updatePersonalStatementDto.getAnswer2());
        assertThat(updatedProfile.getAnswer3()).isEqualTo(updatePersonalStatementDto.getAnswer3());
        assertThat(updatedProfile.getResume()).isEqualTo(updatePersonalStatementDto.getResume());
    }

    @Test
    @DisplayName("자기소개서, 이력서 삭제하기")
    void delete() {
        profileRepository.delete(savedProfile.getUserId());

        assertThatThrownBy(() -> profileRepository.findByUserId(savedProfile.getUserId()))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("visibility 설정 조회하기")
    void findVisibility() {
        Boolean isVisible = profileRepository.findVisibilityByUserId(savedProfile.getUserId()); // default true

        assertThat(savedProfile.getIsVisible()).isTrue();
        assertThat(isVisible).isTrue();
    }

    @Test
    @DisplayName("visibility 설정 업데이트하기")
    void updateVisibility() {
        profileRepository.updateVisibility(savedProfile.getUserId(), false);
        ProfessionalProfile updatedProfile = profileRepository.findByUserId(savedProfile.getUserId());

        assertThat(savedProfile.getIsVisible()).isTrue();
        assertThat(updatedProfile.getIsVisible()).isFalse();
    }
}