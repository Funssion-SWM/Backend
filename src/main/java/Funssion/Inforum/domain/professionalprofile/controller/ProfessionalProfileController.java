package Funssion.Inforum.domain.professionalprofile.controller;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.gpt.GptService;
import Funssion.Inforum.domain.professionalprofile.dto.request.SaveProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.dto.response.ProfessionalProfileResponseDto;
import Funssion.Inforum.domain.professionalprofile.service.ProfessionalProfileService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users/profile/professional")
@RequiredArgsConstructor
public class ProfessionalProfileController {

    private final ProfessionalProfileService professionalProfileService;
    private final GptService gptService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProfessionalProfile(
            @RequestBody @Validated SaveProfessionalProfileDto saveProfessionalProfileDto
            ) {
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        professionalProfileService.createProfessionalProfile(userId, saveProfessionalProfileDto);
        gptService.getDescriptionByGPTAndUpdateDescription(userId, getAnswerList(saveProfessionalProfileDto));
    }

    @PutMapping
    public void updateProfessionalProfile(
            @RequestBody @Validated SaveProfessionalProfileDto updatePersonalStatementDto
    ) {
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        professionalProfileService.update(userId, updatePersonalStatementDto);
        gptService.getDescriptionByGPTAndUpdateDescription(userId, getAnswerList(updatePersonalStatementDto));
    }

    @GetMapping("/visibility")
    public Boolean getVisibility() {
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        return professionalProfileService.getVisibility(userId);
    }

    @PostMapping("/visibility")
    public void updateVisibility(@RequestParam Boolean isVisible) {
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        professionalProfileService.updateVisibility(userId, isVisible);
    }

    @PostMapping("/resume/image")
    public void uploadImageInResume(
            @RequestPart MultipartFile image
            ) {

    }

    @GetMapping("/{id}")
    public ProfessionalProfileResponseDto getProfessionalProfile(
            @PathVariable(value = "id") Long userId
    ) {
        return professionalProfileService.getProfessionalProfile(userId);
    }

    @NotNull
    private static List<String> getAnswerList(SaveProfessionalProfileDto saveProfessionalProfileDto) {
        return List.of(saveProfessionalProfileDto.getAnswer1(), saveProfessionalProfileDto.getAnswer2(), saveProfessionalProfileDto.getAnswer3());
    }
}
