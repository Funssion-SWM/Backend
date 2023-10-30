package Funssion.Inforum.domain.employer.service;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.employer.dto.EmployeeDto;
import Funssion.Inforum.domain.employer.dto.EmployerLikesEmployee;
import Funssion.Inforum.domain.employer.dto.EmployerProfile;
import Funssion.Inforum.domain.employer.dto.EmployerUnlikesEmployee;
import Funssion.Inforum.domain.employer.repository.EmployerRepository;
import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static Funssion.Inforum.common.constant.NotificationType.NEW_EMPLOYER;

@Service
@RequiredArgsConstructor
public class EmployerService {
    private final EmployerRepository employerRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesLookingForJob(Long page){
        return employerRepository.getEmployeesLookingForJob(page)
                .stream()
                .map(employee -> new EmployeeDto(employee))
                .collect(Collectors.toList());
    }
    @Transactional
    public EmployerLikesEmployee likeEmployee(Long employeeId){
        Long employerId = SecurityContextUtils.getAuthorizedUserId();
        if(employerRepository.doesEmployerLikeEmployee(employerId,employeeId)) throw new BadRequestException("이미 관심 등록한 지원자입니다.");

        EmployerProfile senderProfile = employerRepository.getEmployerProfile(employerId);
        saveNotification(employeeId, senderProfile);
        return employerRepository.likeEmployee(employeeId);
    }

    private void saveNotification(Long userId, EmployerProfile senderProfile) {
        notificationRepository.save(
                Notification.builder()
                        .receiverId(userId)
                        .senderId(senderProfile.getEmployerId())
                        .senderName(senderProfile.getCompanyName())
                        .senderImagePath(senderProfile.getImagePath())
                        .senderRank("EMPLOYER")
                        .notificationType(NEW_EMPLOYER)
                        .build()
        );
    }

    @Transactional
    public EmployerUnlikesEmployee unlikesEmployee(Long employeeId){
        Long employerId = SecurityContextUtils.getAuthorizedUserId();
        if(employerRepository.doesEmployerLikeEmployee(employerId,employeeId).equals(false)) throw new BadRequestException("관심 등록하지 않은 지원자입니다.");

        notificationRepository.deleteEmployerNotification(employerId,employeeId);
        return employerRepository.unlikeEmployee(employeeId);

    }

}
