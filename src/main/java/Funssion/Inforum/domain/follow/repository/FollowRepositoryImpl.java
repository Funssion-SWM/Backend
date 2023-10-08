package Funssion.Inforum.domain.follow.repository;

import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.follow.domain.Follow;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class FollowRepositoryImpl implements FollowRepository {

    private final JdbcTemplate template;

    public FollowRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public void save(Follow follow) {
        String sql = "insert into member.follow (user_id, followed_user_id) " +
                "values(?, ?)";

        template.update(sql, follow.getUserId(), follow.getFollowedUserId());
    }

    @Override
    public void delete(Long userId, Long followedUserId) {
        String sql = "delete from member.follow where user_id = ? and followed_user_id = ?";

        if (template.update(sql, userId, followedUserId) == 0)
            throw new NotFoundException("해당 팔로우 정보가 존재하지 않습니다.");
    }

    @Override
    public Optional<Follow> findByUserIdAndFollowedUserId(Long userId, Long followedUserId) {
        String sql = "select * from member.follow where user_id = ? and followed_user_id = ?";

        return template.query(sql, followRowMapper(), userId, followedUserId).stream().findAny();
    }

    private RowMapper<Follow> followRowMapper() {
        return (rs, rowNum) -> Follow.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .followedUserId(rs.getLong("followed_user_id"))
                .created(rs.getTimestamp("created").toLocalDateTime())
                .build();
    }

    @Override
    public List<MemberProfileEntity> findFollowingProfilesByUserId(Long userId) {
        String sql = "select i.id, name, introduce, tags, image_path, follow_cnt, follower_cnt from member.follow f, member.info i where f.user_id = ? and f.followed_user_id = i.id order by f.created desc";

        return template.query(sql, MemberProfileEntity.MemberInfoRowMapper(),userId);
    }

    @Override
    public List<MemberProfileEntity> findFollowedProfilesByUserId(Long userId) {
        String sql = "select i.id, name, introduce, tags, image_path, follow_cnt, follower_cnt from member.follow f, member.info i where f.followed_user_id = ? and f.user_id = i.id order by f.created desc";

        return template.query(sql, MemberProfileEntity.MemberInfoRowMapper(),userId);
    }
}
