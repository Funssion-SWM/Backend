package Funssion.Inforum.domain.notification.domain;

import Funssion.Inforum.common.constant.NotificationType;
import Funssion.Inforum.common.constant.PostType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode(exclude = {"created", "id"})
@RequiredArgsConstructor
public class Notification {
    private final Long id;
    private final Long receiverId;
    private final PostType postTypeToShow;
    private final Long postIdToShow;
    private final Long senderId;
    private final String senderName;
    private final String senderImagePath;
    private final String senderRank;
    private final PostType senderPostType;
    private final Long senderPostId;
    private final NotificationType notificationType;
    private final Boolean isChecked;
    private final LocalDateTime created;
}
