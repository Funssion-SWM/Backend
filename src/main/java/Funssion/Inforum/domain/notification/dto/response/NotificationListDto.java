package Funssion.Inforum.domain.notification.dto.response;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.post.comment.dto.response.PostIdAndTypeInfo;
import Funssion.Inforum.domain.post.domain.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class NotificationListDto {
    private final Long id;
    private final Long senderId;
    private final String senderName;
    private final String senderImagePath;
    private final String message;
    private final Boolean isChecked;
    private final LocalDateTime created;
    private final PostType postTypeToShow;
    private final Long postIdToShow;

    public static NotificationListDto valueOf(Notification notification) {
        return NotificationListDto.builder()
                .id(notification.getId())
                .senderId(notification.getSenderId())
                .senderName(notification.getSenderName())
                .senderImagePath(notification.getSenderImagePath())
                .message(notification.getNotificationType().getMessage())
                .isChecked(notification.getIsChecked())
                .created(notification.getCreated())
                .postTypeToShow(notification.getPostTypeToShow())
                .postIdToShow(notification.getPostIdToShow())
                .build();
    }
}
