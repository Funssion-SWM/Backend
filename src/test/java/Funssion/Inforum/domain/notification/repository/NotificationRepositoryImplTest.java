package Funssion.Inforum.domain.notification.repository;

import Funssion.Inforum.common.constant.NotificationType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.notification.domain.Notification;
import Funssion.Inforum.domain.score.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class NotificationRepositoryImplTest {

    @Autowired
    NotificationRepository repository;

    Long userId1 = 1L;
    Long userId2 = 2L;

    Notification newCommentNotification =
            Notification.builder()
                    .receiverId(userId1)
                    .postTypeToShow(PostType.MEMO)
                    .postIdToShow(1L)
                    .senderId(userId2)
                    .senderName("jinu")
                    .senderImagePath("https://image")
                    .senderRank(Rank.BRONZE_5.toString())
                    .senderPostType(PostType.COMMENT)
                    .senderPostId(1L)
                    .notificationType(NotificationType.NEW_COMMENT)
                    .isChecked(false)
                    .build();

    Notification newAnswerNotification =
            Notification.builder()
                    .receiverId(userId1)
                    .postTypeToShow(PostType.QUESTION)
                    .postIdToShow(1L)
                    .senderId(userId2)
                    .senderName("jinu")
                    .senderImagePath("https://image")
                    .senderRank(Rank.BRONZE_5.toString())
                    .senderPostType(PostType.ANSWER)
                    .senderPostId(1L)
                    .notificationType(NotificationType.NEW_ANSWER)
                    .isChecked(false)
                    .build();

    Notification newQuestionNotification =
            Notification.builder()
                    .receiverId(userId1)
                    .postTypeToShow(PostType.MEMO)
                    .postIdToShow(1L)
                    .senderId(userId2)
                    .senderName("jinu")
                    .senderRank(Rank.BRONZE_5.toString())
                    .senderImagePath("https://image")
                    .senderPostType(PostType.QUESTION)
                    .senderPostId(1L)
                    .notificationType(NotificationType.NEW_QUESTION)
                    .isChecked(false)
                    .build();

    Notification newFollowerNotification =
            Notification.builder()
                    .receiverId(userId1)
                    .senderId(userId2)
                    .senderName("jinu")
                    .senderRank(Rank.BRONZE_5.toString())
                    .senderImagePath("https://image")
                    .notificationType(NotificationType.NEW_FOLLOWER)
                    .isChecked(false)
                    .build();

    Notification newPostFollowedNotification =
            Notification.builder()
                    .receiverId(userId1)
                    .postTypeToShow(PostType.MEMO)
                    .postIdToShow(1L)
                    .senderId(userId2)
                    .senderName("jinu")
                    .senderRank(Rank.BRONZE_5.toString())
                    .senderImagePath("https://image")
                    .senderPostType(PostType.MEMO)
                    .senderPostId(1L)
                    .notificationType(NotificationType.NEW_POST_FOLLOWED)
                    .isChecked(false)
                    .build();

    Notification newAcceptedNotification =
            Notification.builder()
                    .receiverId(userId1)
                    .postTypeToShow(PostType.QUESTION)
                    .postIdToShow(2L)
                    .senderId(userId2)
                    .senderName("jinu")
                    .senderPostId(2L)
                    .senderRank(Rank.BRONZE_5.toString())
                    .senderPostType(PostType.QUESTION)
                    .senderImagePath("https://image")
                    .notificationType(NotificationType.NEW_ACCEPTED)
                    .isChecked(false)
                    .build();

    @BeforeEach
    void init() {
        repository.save(newCommentNotification);
        repository.save(newQuestionNotification);
        repository.save(newAnswerNotification);
        repository.save(newFollowerNotification);
        repository.save(newPostFollowedNotification);
        repository.save(newAcceptedNotification);
    }

    @Test
    @DisplayName("30일 이내 유저 알림 최대 20개 가져오기")
    void findAllByUserId() {
        List<Notification> notifications = repository.find30DaysNotificationsMaximum20ByUserId(userId1);

        assertThat(notifications).containsExactly(
                newAcceptedNotification, newPostFollowedNotification,
                newFollowerNotification, newAnswerNotification,
                newQuestionNotification, newCommentNotification);

        for(int i = 0 ; i < 20 ; i++) {
            repository.save(newCommentNotification);
        }

        List<Notification> notificationsMaximum20 = repository.find30DaysNotificationsMaximum20ByUserId(userId1);

        assertThat(notificationsMaximum20).hasSize(20);
        assertThat(notificationsMaximum20).containsOnly(newCommentNotification);
    }

    @Nested
    @DisplayName("알림 삭제하기")
    class delete {

        @Test
        @DisplayName("팔로우, 답변 채택을 제외한 알림 삭제")
        void deleteExcludingNewFollowerNotification() {
            List<Notification> notifications = repository.find30DaysNotificationsMaximum20ByUserId(userId1);
            repository.delete(newPostFollowedNotification.getSenderPostType(), newPostFollowedNotification.getSenderPostId());
            repository.delete(newCommentNotification.getSenderPostType(), newCommentNotification.getSenderPostId());
            repository.delete(newAnswerNotification.getSenderPostType(), newAnswerNotification.getSenderPostId());
            repository.delete(newQuestionNotification.getSenderPostType(), newQuestionNotification.getSenderPostId());

            List<Notification> remainNotifications = repository.find30DaysNotificationsMaximum20ByUserId(userId1);

            assertThat(notifications).containsExactly(
                    newAcceptedNotification, newPostFollowedNotification,
                    newFollowerNotification, newAnswerNotification,
                    newQuestionNotification, newCommentNotification);

            assertThat(remainNotifications).containsExactly(
                    newAcceptedNotification, newFollowerNotification
            );
        }

        @Test
        @DisplayName("팔로우 알림 삭제")
        void deleteNewFollowerNotification() {
            List<Notification> notifications = repository.find30DaysNotificationsMaximum20ByUserId(userId1);
            repository.deleteFollowNotification(userId1, userId2);

            List<Notification> remainNotifications = repository.find30DaysNotificationsMaximum20ByUserId(userId1);

            assertThat(notifications).containsExactly(
                    newAcceptedNotification, newPostFollowedNotification,
                    newFollowerNotification, newAnswerNotification,
                    newQuestionNotification, newCommentNotification);

            assertThat(remainNotifications).containsExactly(
                    newAcceptedNotification, newPostFollowedNotification,
                    newAnswerNotification, newQuestionNotification,
                    newCommentNotification);
        }

    }

    @Test
    @DisplayName("알림 체크하기")
    void updateIsCheckedToTrue() {
        List<Notification> unCheckedNotificationList = repository.find30DaysNotificationsMaximum20ByUserId(userId1);
        repository.updateIsCheckedToTrue(userId1);
        List<Notification> checkedNotificationList = repository.find30DaysNotificationsMaximum20ByUserId(userId1);

        assertThat(unCheckedNotificationList).isNotSameAs(checkedNotificationList);
        for (Notification checkedNotification : checkedNotificationList) {
            assertThat(checkedNotification.getIsChecked()).isTrue();
        }
    }
}