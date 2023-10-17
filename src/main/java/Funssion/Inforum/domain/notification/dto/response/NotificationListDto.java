package Funssion.Inforum.domain.notification.dto.response;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.post.domain.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class NotificationListDto {
    private final Long id;
    private final Long senderId;
    private final String senderName;
    private final String senderImagePath;
    private final String message;
    private final LocalDateTime created;
    private PostType postTypeToShow;
    private Long postIdToShow;

    @Builder
    public NotificationListDto(Long id, Long senderId, String senderName, String senderImagePath, String message, LocalDateTime created) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderImagePath = senderImagePath;
        this.message = message;
        this.created = created;
    }

    public static NotificationListDto valueOf(Notification notification) {
        return NotificationListDto.builder()
                .id(notification.getId())
                .senderId(notification.getSenderId())
                .senderName(notification.getSenderName())
                .senderImagePath(notification.getSenderImagePath())
                .message(notification.getNotificationType().getMessage())
                .created(notification.getCreated())
                .build();
    }

    public void setPostInfoToShow(PostType postTypeToShow, Long postIdToShow) {
        this.postTypeToShow = postTypeToShow;
        this.postIdToShow = postIdToShow;
    }
}
