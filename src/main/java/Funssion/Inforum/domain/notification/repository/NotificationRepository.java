package Funssion.Inforum.domain.notification.repository;

import Funssion.Inforum.common.constant.NotificationType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.notification.domain.Notification;

import java.util.List;

public interface NotificationRepository {

    void save(Notification notification);
    void delete(PostType senderPostType, Long senderPostId);
    void deleteFollowNotification(Long receiverId, Long senderId);
    List<Notification> find30DaysNotificationsMaximum20ByUserId(Long receiverId);
}
