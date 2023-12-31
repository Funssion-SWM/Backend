package Funssion.Inforum.domain.notification.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.notification.domain.Notification;

import java.util.List;

public interface NotificationRepository {

    void save(Notification notification);
    void updateIsCheckedToTrue(Long userId);
    void delete(PostType senderPostType, Long senderPostId);
    void deleteFollowNotification(Long receiverId, Long senderId);
    void deleteEmployerNotification(Long employerId, Long employeeId);
    List<Notification> find30DaysNotificationsMaximum20ByUserId(Long receiverId);
}
