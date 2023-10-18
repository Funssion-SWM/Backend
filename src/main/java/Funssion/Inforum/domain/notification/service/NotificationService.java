package Funssion.Inforum.domain.notification.service;

import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.notification.dto.response.NotificationListDto;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import Funssion.Inforum.domain.post.comment.dto.response.PostIdAndTypeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationListDto> getNotifications(Long userId) {
        List<NotificationListDto> notificationList = notificationRepository.find30DaysNotificationsMaximum20ByUserId(userId).stream()
                .map(notification -> {
                    NotificationListDto notificationListDto = NotificationListDto.valueOf(notification);
                    notificationListDto.setPostInfoToShow(getPostInfo(notification));
                    return notificationListDto;
                })
                .toList();
        return null;
    }

    private PostIdAndTypeInfo getPostInfo(Notification notification) {
        return null;
    }

    public void checkNotifications(Long userId) {

    }
}
