package Funssion.Inforum.domain.profile;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.profile.domain.AuthorProfile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    private final JdbcTemplate template;

    public ProfileRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public void updateProfile(Long userId, MemberProfileEntity memberProfile) {
        updateAuthorProfile(userId, memberProfile);
        updateUserProfile(userId, memberProfile);
    }

    private void updateAuthorProfile(Long userId, MemberProfileEntity memberProfile) {
        for (PostType postType : PostType.values()) {
            String sql = "UPDATE post." + postType.getValue() + " " +
                    "SET author_image_path = ?, author_name = ? " +
                    "WHERE author_id = ?";

            template.update(sql, memberProfile.getProfileImageFilePath(), memberProfile.getNickname(), userId);
        }
    }

    private void updateUserProfile(Long userId, MemberProfileEntity memberProfile) {
        String sql = "update member.info " +
                "set image_path = ?, name = ?, introduce = ? " +
                "where id = ?";

        template.update(sql, memberProfile.getProfileImageFilePath(), memberProfile.getNickname(), memberProfile.getIntroduce(), userId);
    }

    @Override
    public void updateAuthorImagePathInPost(Long userId, String newImageURL) {
        for (PostType postType : PostType.values()) {
            String sql = "UPDATE post." + postType.getValue() + " " +
                    "SET author_image_path = ? " +
                    "WHERE author_id = ?";

            template.update(sql, newImageURL, userId);
        }
    }

    @Override
    public AuthorProfile findAuthorProfile(PostType postType, Long postId) {
        String sql = "SELECT author_id, author_name, author_image_path " +
                "FROM post." + postType.getValue() + " " +
                "WHERE id = ?";

        return template.queryForObject(sql, authorProfileRowMapper(), postId);
    }

    private RowMapper<AuthorProfile> authorProfileRowMapper() {
        return (rs, rowNum) -> AuthorProfile.builder()
                .id(rs.getLong("author_id"))
                .name(rs.getString("author_name"))
                .profileImagePath("author_image_path")
                .build();
    }

    @Override
    public Long findAuthorId(PostType postType, Long postId) {
        String sql = "SELECT author_id " +
                "FROM post." + postType.getValue() + " " +
                "WHERE id = ?";

        return template.queryForObject(sql, Long.class, postId);
    }
}
