package Funssion.Inforum.domain.profile;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.tag.repository.TagRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ProfileRepository {

    private final JdbcTemplate template;

    public ProfileRepository(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

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
}
