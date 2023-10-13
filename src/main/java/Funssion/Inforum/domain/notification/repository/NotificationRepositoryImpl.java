package Funssion.Inforum.domain.notification.repository;

import Funssion.Inforum.common.constant.NotificationType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.notification.domain.Notification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JdbcTemplate template;

    public NotificationRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public void save(Notification notification) {
        String sql = "insert into member.notification(receiver_id, receiver_post_type, receiver_post_id, sender_id, sender_name, sender_image_path, sender_post_type, sender_post_id, notification_type) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        template.update(sql, getAllParams(notification));
    }

    private Object[] getAllParams(Notification notification) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(notification.getReceiverId());
        params.add(nullableEnumToString(notification.getReceiverPostType()));
        params.add(notification.getReceiverPostId());
        params.add(notification.getSenderId());
        params.add(notification.getSenderName());
        params.add(notification.getSenderImagePath());
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
    public void delete(PostType postType, Long postId) {
        String sql = "delete from member.notification where post_type = ? and post_id = ?";

        template.update(sql, postType, postId);
    }

    @Override
    public List<Notification> find30DaysNotificationsMaximum20ByUserId(Long userId) {
        String sql = "select * from member.notification where receiver_id = ? and created > current_timestamp  - interval '30 days' order by id desc limit 20";

        return template.query(sql, notificationRowMapper() ,userId);
    }

    private RowMapper<Notification> notificationRowMapper() {
        return (rs, rowNum) -> Notification.builder()
                .id(rs.getLong("id"))
                .receiverId(rs.getLong("receiver_id"))
                .receiverPostType(PostType.of(rs.getString("receiver_post_type")))
                .receiverPostId(rs.getLong("receiver_post_id"))
                .senderId(rs.getLong("sender_id"))
                .senderName(rs.getString("sender_name"))
                .senderImagePath(rs.getString("sender_image_path"))
                .senderPostType(PostType.of(rs.getString("sender_post_type")))
                .senderPostId(rs.getLong("sender_post_id"))
                .notificationType(NotificationType.of(rs.getString("notification_type")))
                .created(rs.getTimestamp("created").toLocalDateTime())
                .build();
    }
}