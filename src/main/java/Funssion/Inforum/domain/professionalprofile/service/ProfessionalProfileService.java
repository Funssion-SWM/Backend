package Funssion.Inforum.domain.professionalprofile.service;

import Funssion.Inforum.common.constant.Role;
import Funssion.Inforum.common.exception.forbidden.ForbiddenException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import Funssion.Inforum.domain.professionalprofile.dto.request.SaveProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.dto.response.ProfessionalProfileResponseDto;
import Funssion.Inforum.domain.professionalprofile.repository.ProfessionalProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfessionalProfileService {

    private final ProfessionalProfileRepository professionalProfileRepository;

    public ProfessionalProfileResponseDto getProfessionalProfile(Long userId) {
        Long clientId = SecurityContextUtils.getUserId();
        String clientRole = SecurityContextUtils.getAuthority();
        log.info("clientRole={}",clientRole);

        validateAccess(userId, clientId, clientRole);

        return ProfessionalProfileResponseDto.valueOf(
                professionalProfileRepository.findByUserId(userId)
        );
    }

    private void validateAccess(Long userId, Long clientId, String clientRole) {
        ProfessionalProfile professionalProfile;
        try{
            professionalProfile = professionalProfileRepository.findByUserId(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("해당 유저의 정보를 찾을 수 없습니다.");
        }

        if (!professionalProfile.getIsVisible() && !clientId.equals(userId))
            throw new ForbiddenException("비공개 설정된 프로필은 열람할 수 없습니다.");

        if (!clientId.equals(userId) && !clientRole.equals(Role.EMPLOYER.getRoles()))
            throw new ForbiddenException("일반 유저는 접근할 수 없습니다");
    }

    public void createProfessionalProfile(Long userId, SaveProfessionalProfileDto saveProfessionalProfileDto) {
        professionalProfileRepository.create(userId, saveProfessionalProfileDto);
    }

    public void update(Long userId, SaveProfessionalProfileDto saveProfessionalProfileDto) {
        professionalProfileRepository.update(userId, saveProfessionalProfileDto);
    }

    public void updateVisibility(Long userId, Boolean isVisible) {
        professionalProfileRepository.updateVisibility(userId, isVisible);
    }
}
