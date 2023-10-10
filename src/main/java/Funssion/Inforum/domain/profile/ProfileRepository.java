package Funssion.Inforum.domain.profile;

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
        updateAuthorProfileInMemo(userId, memberProfile);
        updateAuthorProfileInComment(userId, memberProfile);
        updateAuthorProfileInReComment(userId, memberProfile);
        updateAuthorProfileInQuestion(userId, memberProfile);
        updateAuthorProfileInAnswer(userId, memberProfile);
        updateUserProfile(userId, memberProfile);
    }

    private void updateAuthorProfileInMemo(Long userId, MemberProfileEntity memberProfile) {
        String sql = "update memo.info " +
                "set author_image_path = ?, author_name = ? " +
                "where author_id = ?";

        template.update(sql, memberProfile.getProfileImageFilePath(), memberProfile.getNickname(), userId);
    }

    private void updateAuthorProfileInComment(Long userId, MemberProfileEntity memberProfile) {
        String sql = "update comment.info " +
                "set author_image_path = ?, author_name = ? " +
                "where author_id = ?";

        template.update(sql, memberProfile.getProfileImageFilePath(), memberProfile.getNickname(), userId);
    }

    private void updateAuthorProfileInReComment(Long userId, MemberProfileEntity memberProfile) {
        String sql = "update comment.re_comments " +
                "set author_image_path = ?, author_name = ? " +
                "where author_id = ?";

        template.update(sql, memberProfile.getProfileImageFilePath(), memberProfile.getNickname(), userId);
    }

    private void updateAuthorProfileInQuestion(Long userId, MemberProfileEntity memberProfile) {
        String sql = "update question.info " +
                "set author_image_path = ?, author_name = ? " +
                "where author_id = ?";

        template.update(sql, memberProfile.getProfileImageFilePath(), memberProfile.getNickname(), userId);
    }

    private void updateAuthorProfileInAnswer(Long userId, MemberProfileEntity memberProfile) {
        String sql = "update question.answer " +
                "set author_image_path = ?, author_name = ? " +
                "where author_id = ?";

        template.update(sql, memberProfile.getProfileImageFilePath(), memberProfile.getNickname(), userId);
    }

    private void updateUserProfile(Long userId, MemberProfileEntity memberProfile) {
        String sql = "update member.info " +
                "set image_path = ?, name = ?, introduce = ? " +
                "where id = ?";

        template.update(sql, memberProfile.getProfileImageFilePath(), memberProfile.getNickname(), memberProfile.getIntroduce(), userId);
    }
}
