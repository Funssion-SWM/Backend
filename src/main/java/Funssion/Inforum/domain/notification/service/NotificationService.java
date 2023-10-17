package Funssion.Inforum.domain.notification.service;

import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.notification.dto.response.NotificationListDto;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationListDto> getNotifications(Long userId) {
        List<NotificationListDto> notificationList = notificationRepository.find30DaysNotificationsMaximum20ByUserId(userId).stream()
                .map(NotificationListDto::valueOf)
                .toList();

        return null;
    }

    public void checkNotifications(Long userId) {

    }
}
