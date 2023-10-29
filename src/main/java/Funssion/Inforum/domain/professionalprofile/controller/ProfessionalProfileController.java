package Funssion.Inforum.domain.professionalprofile.controller;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.professionalprofile.dto.request.SaveProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.dto.response.ProfessionalProfileResponseDto;
import Funssion.Inforum.domain.professionalprofile.service.ProfessionalProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professional-profile")
@RequiredArgsConstructor
public class ProfessionalProfileController {

    private final ProfessionalProfileService professionalProfileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProfessionalProfile(
            @RequestBody @Validated SaveProfessionalProfileDto saveProfessionalProfileDto
            ) {
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        professionalProfileService.createProfessionalProfile(userId, saveProfessionalProfileDto);
    }

    @PutMapping
    public void updateProfessionalProfile(
            @RequestBody @Validated SaveProfessionalProfileDto updatePersonalStatementDto
    ) {
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        professionalProfileService.update(userId, updatePersonalStatementDto);
    }

    @PostMapping("/visibility")
    public void updateVisibility(@RequestParam Boolean isVisible) {
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        professionalProfileService.updateVisibility(userId, isVisible);
    }


    @GetMapping("/{id}")
    public ProfessionalProfileResponseDto getProfessionalProfile(
            @PathVariable(value = "id") Long userId
    ) {
        return professionalProfileService.getProfessionalProfile(userId);
    }
}
