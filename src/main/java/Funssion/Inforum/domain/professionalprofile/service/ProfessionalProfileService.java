package Funssion.Inforum.domain.professionalprofile.service;

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
}
