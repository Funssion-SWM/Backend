package Funssion.Inforum.domain.professionalprofile.service;

import Funssion.Inforum.common.exception.forbidden.ForbiddenException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import Funssion.Inforum.domain.professionalprofile.dto.request.CreateProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.dto.request.UpdatePersonalStatementDto;
import Funssion.Inforum.domain.professionalprofile.dto.request.UpdateResumeDto;
import Funssion.Inforum.domain.professionalprofile.dto.response.ProfessionalProfileResponseDto;
import Funssion.Inforum.domain.professionalprofile.repository.ProfessionalProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfessionalProfileService {

    private final ProfessionalProfileRepository professionalProfileRepository;

    public ProfessionalProfileResponseDto getProfessionalProfile(Long userId) {
        Long clientId = SecurityContextUtils.getUserId();
        ProfessionalProfile professionalProfile = professionalProfileRepository.findByUserId(userId);

        if (!professionalProfile.getIsVisible() && !clientId.equals(userId))
            throw new ForbiddenException("비공개 설정된 프로필은 열람할 수 없습니다.");

        return ProfessionalProfileResponseDto.valueOf(
                professionalProfileRepository.findByUserId(userId)
        );
    }

    public void createProfessionalProfile(Long userId, CreateProfessionalProfileDto createProfessionalProfileDto) {
        professionalProfileRepository.create(userId, createProfessionalProfileDto);
    }

    public void updatePersonalStatement(Long userId, UpdatePersonalStatementDto updatePersonalStatementDto) {
        professionalProfileRepository.updatePersonalStatement(userId, updatePersonalStatementDto);
    }

    public void updateResume(Long userId, UpdateResumeDto updateResumeDto) {
        professionalProfileRepository.updateResume(userId, updateResumeDto);
    }

    public void updateVisibility(Long userId, Boolean isVisible) {
        professionalProfileRepository.updateVisibility(userId, isVisible);
    }
}
