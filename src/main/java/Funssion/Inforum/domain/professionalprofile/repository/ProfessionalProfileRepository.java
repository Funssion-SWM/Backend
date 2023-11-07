package Funssion.Inforum.domain.professionalprofile.repository;

import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import Funssion.Inforum.domain.professionalprofile.dto.request.SaveProfessionalProfileDto;

import java.util.List;

public interface ProfessionalProfileRepository {

    void create(Long userId, SaveProfessionalProfileDto professionalProfile);
    ProfessionalProfile findByUserId(Long userId);
    Boolean findVisibilityByUserId(Long userId);
    void update(Long userId, SaveProfessionalProfileDto professionalProfile);
    void updateDescription(Long userId, String description);
    void updateVisibility(Long userId, Boolean isVisible);
    void delete(Long userId);
}
