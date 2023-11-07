package Funssion.Inforum.domain.professionalprofile.service;

import Funssion.Inforum.common.constant.Role;
import Funssion.Inforum.common.exception.forbidden.ForbiddenException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import Funssion.Inforum.domain.professionalprofile.dto.request.SaveProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.dto.response.ProfessionalProfileResponseDto;
import Funssion.Inforum.domain.professionalprofile.repository.ProfessionalProfileRepository;
import Funssion.Inforum.s3.S3Repository;
import Funssion.Inforum.s3.S3Utils;
import Funssion.Inforum.s3.dto.response.ImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

import static Funssion.Inforum.common.constant.CRUDType.UPDATE;
import static Funssion.Inforum.common.constant.Role.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfessionalProfileService {

    @Value("${aws.s3.resume-dir}")
    private String RESUME_DIR;
    private final ProfessionalProfileRepository professionalProfileRepository;
    private final S3Repository s3Repository;

    public ProfessionalProfileResponseDto getProfessionalProfile(Long userId) {
        ProfessionalProfile validatedProfile = getValidatedProfile(userId);

        return ProfessionalProfileResponseDto.valueOf(
                validatedProfile
        );
    }

    private ProfessionalProfile getValidatedProfile(Long userId) {
        ProfessionalProfile professionalProfile = getNonNullProfile(userId);

        Long clientId = SecurityContextUtils.getAuthorizedUserId();

        if (!professionalProfile.getIsVisible() && !clientId.equals(userId))
            throw new ForbiddenException("비공개 설정된 프로필은 열람할 수 없습니다.");

        if (!clientId.equals(userId) && !EMPLOYER.isEqualTo(SecurityContextUtils.getAuthorities()))
            throw new ForbiddenException("일반 유저는 접근할 수 없습니다");

        return professionalProfile;
    }

    private ProfessionalProfile getNonNullProfile(Long userId) {
        ProfessionalProfile professionalProfile;

        try{
            professionalProfile = professionalProfileRepository.findByUserId(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("해당 유저의 정보를 찾을 수 없습니다.");
        }
        return professionalProfile;
    }

    public void createProfessionalProfile(Long userId, SaveProfessionalProfileDto saveProfessionalProfileDto) {
        professionalProfileRepository.create(userId, saveProfessionalProfileDto);
    }

    public void update(Long userId, SaveProfessionalProfileDto saveProfessionalProfileDto) {
        professionalProfileRepository.update(userId, saveProfessionalProfileDto);
    }

    public Boolean getVisibility(Long userId) {
        return getNonNullVisibility(userId);
    }

    private Boolean getNonNullVisibility(Long userId) {
        try {
            return professionalProfileRepository.findVisibilityByUserId(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("해당 유저의 정보를 찾을 수 없습니다.");
        }
    }


    public void updateVisibility(Long userId, Boolean isVisible) {
        professionalProfileRepository.updateVisibility(userId, isVisible);
    }

    public ImageDto uploadImageInResume(Long userId, MultipartFile image) {

        String imageName = S3Utils.generateImageNameOfS3(userId);

        String bucketName = s3Repository.createFolder(RESUME_DIR, userId.toString());
        String uploadedURL = s3Repository.upload(image, bucketName, imageName);

        return ImageDto.builder()
                .imageName(imageName)
                .imagePath(uploadedURL)
                .build();
    }
}
