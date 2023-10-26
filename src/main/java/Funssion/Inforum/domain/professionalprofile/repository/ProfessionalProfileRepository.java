package Funssion.Inforum.domain.professionalprofile.repository;

import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import Funssion.Inforum.domain.professionalprofile.dto.request.CreateProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.dto.request.UpdatePersonalStatementDto;
import Funssion.Inforum.domain.professionalprofile.dto.request.UpdateResumeDto;

public interface ProfessionalProfileRepository {

    void create(Long userId, CreateProfessionalProfileDto professionalProfile);
    ProfessionalProfile findByUserId(Long userId);
    void updatePersonalStatement(Long userId, UpdatePersonalStatementDto personalStatement);
    void updateResume(Long userId, UpdateResumeDto resume);
    void delete(Long userId);
}
