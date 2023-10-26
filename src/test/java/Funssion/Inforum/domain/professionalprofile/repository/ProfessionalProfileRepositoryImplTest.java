package Funssion.Inforum.domain.professionalprofile.repository;

import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.Member;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import Funssion.Inforum.domain.professionalprofile.dto.request.CreateProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.dto.request.UpdatePersonalStatementDto;
import Funssion.Inforum.domain.professionalprofile.dto.request.UpdateResumeDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProfessionalProfileRepositoryImplTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProfessionalProfileRepository profileRepository;

    SaveMemberResponseDto createdMember;
    ProfessionalProfile savedProfile;
    CreateProfessionalProfileDto createProfessionalProfileDto = CreateProfessionalProfileDto.builder()
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
        createdMember = memberRepository.save(
                SocialMember.createSocialMember("test@gmail.com", "jinu")
        );

        profileRepository.create(
                createdMember.getId(),
                createProfessionalProfileDto
        );

        savedProfile = profileRepository.findByUserId(createdMember.getId());
    }

    @Test
    @DisplayName("자기소개서 수정하기")
    void updatePersonalStatement() {
        UpdatePersonalStatementDto updatePersonalStatementDto = UpdatePersonalStatementDto.builder()
                .introduce("updated")
                .techStack("{\"updated\": 3}")
                .description("updated")
                .answer1("updated")
                .answer2("updated")
                .answer3("updated")
                .build();

        profileRepository.updatePersonalStatement(savedProfile.getUserId(), updatePersonalStatementDto);

        ProfessionalProfile updatedProfile = profileRepository.findByUserId(savedProfile.getUserId());

        assertThat(updatedProfile.getIntroduce()).isEqualTo(updatePersonalStatementDto.getIntroduce());
        assertThat(updatedProfile.getTechStack()).isEqualTo(updatePersonalStatementDto.getTechStack());
        assertThat(updatedProfile.getDescription()).isEqualTo(updatePersonalStatementDto.getDescription());
        assertThat(updatedProfile.getAnswer1()).isEqualTo(updatePersonalStatementDto.getAnswer1());
        assertThat(updatedProfile.getAnswer2()).isEqualTo(updatePersonalStatementDto.getAnswer2());
        assertThat(updatedProfile.getAnswer3()).isEqualTo(updatePersonalStatementDto.getAnswer3());
        //resume는 원본과 같은지
        assertThat(updatedProfile.getResume()).isEqualTo(savedProfile.getResume());
    }

    @Test
    @DisplayName("이력서 수정하기")
    void updateResume() {
        UpdateResumeDto updateResumeDto = new UpdateResumeDto("updated");

        profileRepository.updateResume(createdMember.getId(), updateResumeDto);

        ProfessionalProfile updated = profileRepository.findByUserId(createdMember.getId());

        assertThat(updated).hasFieldOrPropertyWithValue("resume", "updated");
    }

    @Test
    @DisplayName("자기소개서, 이력서 삭제하기")
    void delete() {
        profileRepository.delete(createdMember.getId());

        assertThatThrownBy(() -> profileRepository.findByUserId(createdMember.getId()))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}