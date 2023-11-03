package Funssion.Inforum.domain.employer.service;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.employer.domain.Employee;
import Funssion.Inforum.domain.employer.domain.EmployeeWithStatus;
import Funssion.Inforum.domain.employer.domain.InterviewResult;
import Funssion.Inforum.domain.employer.dto.EmployerLikesEmployee;
import Funssion.Inforum.domain.employer.dto.EmployerProfile;
import Funssion.Inforum.domain.employer.dto.EmployerUnlikesEmployee;
import Funssion.Inforum.domain.employer.repository.EmployerRepository;
import Funssion.Inforum.domain.interview.constant.InterviewStatus;
import Funssion.Inforum.domain.interview.repository.InterviewRepository;
import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static Funssion.Inforum.common.constant.NotificationType.NEW_EMPLOYER;

@Service
@RequiredArgsConstructor
public class EmployerService {
    private final EmployerRepository employerRepository;
    private final InterviewRepository interviewRepository;
    private final NotificationRepository notificationRepository;

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

    public List<Employee> getEmployeesOfInterview(Boolean done) {
        return employerRepository.getInterviewEmployees(done);
    }

    public InterviewResult getResultOfInterview(Long employeeId){
        Long employerId = SecurityContextUtils.getAuthorizedUserId();
        if(!interviewRepository.getInterviewStatusOfUser(employerId, employeeId).getStatus().equals(InterviewStatus.DONE.toString()))
            throw new BadRequestException("면접이 완료되지 않은 지원자입니다.");

        return employerRepository.getInterviewResultOf(employeeId);
    }

    public List<EmployeeWithStatus> getLikeEmployees() {
        return employerRepository.getLikeEmployees();
    }
}
