package Funssion.Inforum.domain.notification.controller;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.notification.dto.response.NotificationListDto;
import Funssion.Inforum.domain.notification.service.NotificationService;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationListDto> getNotifications() {
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        return notificationService.getNotifications(userId);
    }


    @PostMapping("/check")
    public void checkNotifications() {
        Long userId = SecurityContextUtils.getAuthorizedUserId();
        notificationService.checkNotifications(userId);
    }

}
