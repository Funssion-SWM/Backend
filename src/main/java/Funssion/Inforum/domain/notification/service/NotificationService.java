package Funssion.Inforum.domain.notification.service;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.notification.dto.response.NotificationListDto;
import Funssion.Inforum.domain.notification.repository.NotificationRepository;
import Funssion.Inforum.domain.post.comment.dto.response.PostIdAndTypeInfo;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static Funssion.Inforum.common.constant.PostType.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<NotificationListDto> getNotifications(Long userId) {
        return notificationRepository.find30DaysNotificationsMaximum20ByUserId(userId).stream()
                .map(NotificationListDto::valueOf)
                .toList();
    }

    @Transactional
    public void checkNotifications(Long userId) {
        notificationRepository.updateIsCheckedToTrue(userId);
    }
}
