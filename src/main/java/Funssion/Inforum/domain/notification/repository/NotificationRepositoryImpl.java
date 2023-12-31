package Funssion.Inforum.domain.notification.repository;

import Funssion.Inforum.common.constant.NotificationType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.etc.DeleteFailException;
import Funssion.Inforum.domain.notification.domain.Notification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static Funssion.Inforum.common.utils.CustomStringUtils.parseNullableStringtoLong;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JdbcTemplate template;

    public NotificationRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public void save(Notification notification) {

        String sql = "insert into member.notification(receiver_id, post_type_to_show, post_id_to_show, sender_id, sender_name, sender_image_path, sender_rank,sender_post_type, sender_post_id, notification_type) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        template.update(sql, getAllParams(notification));
    }

    private Object[] getAllParams(Notification notification) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(notification.getReceiverId());
        params.add(nullableEnumToString(notification.getPostTypeToShow()));
        params.add(notification.getPostIdToShow());
        params.add(notification.getSenderId());
        params.add(notification.getSenderName());
        params.add(notification.getSenderImagePath());
        params.add(notification.getSenderRank());
        params.add(nullableEnumToString(notification.getSenderPostType()));
        params.add(notification.getSenderPostId());
        params.add(nullableEnumToString(notification.getNotificationType()));
        return params.toArray();
    }

    private Object nullableEnumToString(Object type) {
        if (Objects.isNull(type)) return null;
        return type.toString();
    }

    @Override
    public void updateIsCheckedToTrue(Long userId) {
        String sql = "UPDATE member.notification " +
                "SET is_checked = true " +
                "WHERE receiver_id = ?";

        template.update(sql, userId);
    }

    @Override
    public void delete(PostType senderPostType, Long senderPostId) {
        String sql = "DELETE FROM member.notification " +
                "WHERE sender_post_type = ? AND sender_post_id = ?";

        template.update(sql, senderPostType.toString(), senderPostId);
    }

    @Override
    public void deleteFollowNotification(Long receiverId, Long senderId) {
        String sql = "DELETE FROM member.notification " +
                "WHERE receiver_id = ? AND sender_id = ? AND notification_type = 'NEW_FOLLOWER'";

        if (template.update(sql, receiverId, senderId) != 1) throw new DeleteFailException("");
    }

    @Override
    public void deleteEmployerNotification(Long employerId, Long employeeId) {
        String sql = "DELETE FROM member.notification " +
                "WHERE receiver_id = ? AND sender_id = ? AND notification_type = 'NEW_EMPLOYER'";

        if (template.update(sql, employeeId, employerId) != 1) throw new DeleteFailException("");
    }

    @Override
    public List<Notification> find30DaysNotificationsMaximum20ByUserId(Long receiverId) {
        String sql = "SELECT * FROM member.notification " +
                "WHERE receiver_id = ? and created > current_timestamp  - interval '30 days' order by id desc limit 20";

        return template.query(sql, notificationRowMapper() ,receiverId);
    }

    private RowMapper<Notification> notificationRowMapper() {
        return (rs, rowNum) -> Notification.builder()
                .id(rs.getLong("id"))
                .receiverId(rs.getLong("receiver_id"))
                .postTypeToShow(PostType.of(rs.getString("post_type_to_show")))
                .postIdToShow(parseNullableStringtoLong(rs.getString("post_id_to_show")))
                .senderId(rs.getLong("sender_id"))
                .senderName(rs.getString("sender_name"))
                .senderImagePath(rs.getString("sender_image_path"))
                .senderPostType(PostType.of(rs.getString("sender_post_type")))
                .senderPostId(parseNullableStringtoLong(rs.getString("sender_post_id")))
                .notificationType(NotificationType.of(rs.getString("notification_type")))
                .created(rs.getTimestamp("created").toLocalDateTime())
                .senderRank(rs.getString("sender_rank"))
                .isChecked(rs.getBoolean("is_checked"))
                .build();
    }
}
