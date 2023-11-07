package Funssion.Inforum.domain.profile.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.profile.TechStackDto;
import Funssion.Inforum.domain.profile.domain.AuthorProfile;
import Funssion.Inforum.domain.profile.dto.response.UserProfileForEmployer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    private final JdbcTemplate template;

    public ProfileRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public void updateProfile(Long userId, MemberProfileEntity memberProfile) {
        updateAuthorProfile(userId, memberProfile);
        updateSenderProfile(userId, memberProfile);
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

    private void updateSenderProfile(Long userId, MemberProfileEntity memberProfile) {
        String sql = "UPDATE member.notification " +
                "SET sender_image_path = ?, sender_name = ? " +
                "WHERE id = ?";

        template.update(sql, memberProfile.getProfileImageFilePath(), memberProfile.getNickname(), userId);
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

        updateSenderProfile(userId, newImageURL);
    }

    private void updateSenderProfile(Long userId, String newImageURL) {
        String sql = "UPDATE member.notification " +
                "SET sender_image_path = ? " +
                "WHERE id = ?";

        template.update(sql, newImageURL, userId);
    }

    @Override
    public AuthorProfile findAuthorProfile(PostType postType, Long postId) {
        String sql = "SELECT author_id, author_name, author_image_path, author_rank " +
                "FROM post." + postType.getValue() + " " +
                "WHERE id = ?";

        return template.queryForObject(sql, authorProfileRowMapper(), postId);
    }

    private RowMapper<AuthorProfile> authorProfileRowMapper() {
        return (rs, rowNum) -> AuthorProfile.builder()
                .id(rs.getLong("author_id"))
                .name(rs.getString("author_name"))
                .profileImagePath(rs.getString("author_image_path"))
                .rank(rs.getString("author_rank"))
                .build();
    }

    @Override
    public Long findAuthorId(PostType postType, Long postId) {
        String sql = "SELECT author_id " +
                "FROM post." + postType.getValue() + " " +
                "WHERE id = ?";

        return template.queryForObject(sql, Long.class, postId);
    }

    @Override
    public List<UserProfileForEmployer> findUserProfilesForEmployer(TechStackDto techStackDto) {
        ArrayList<Object> paramList = new ArrayList<>();
        paramList.add(techStackDto.getDevelopmentArea());
        String sql = "SELECT *, CASE WHEN EMPLOYER_LIKE.employee_id IS NOT NULL THEN 'true' ELSE 'false' END AS i_like " +
                "FROM (" +
                "   SELECT m.id, m.name, m.email, m.image_path, m.rank, p.introduce, p.development_area, p.tech_stack, p.description, (" +
                "       SELECT 2*count(stack_element)" +
                "       FROM jsonb_array_elements(p.tech_stack) AS stack_element" +
                "       WHERE p.development_area = ? AND stack_element->>'stack' in " + techStackElements(techStackDto.getTechStacks(), paramList) +
                "   ) matched_count" +
                "   FROM member.professional_profile p, member.info m" +
                "   WHERE p.user_id = m.id " +
                ") sub " +
                "LEFT JOIN (" +
                "   SELECT employee_id " +
                "   FROM employer.to_employee " +
                "   WHERE employer_id = ?" +
                ") EMPLOYER_LIKE " +
                "ON sub.id = EMPLOYER_LIKE.employee_id " +
                "WHERE matched_count > 0 " +
                "ORDER BY matched_count desc";

        paramList.add(SecurityContextUtils.getAuthorizedUserId());
        return template.query(sql, userProfileRowMapperForEmployer(), paramList.toArray());
    }

    private String techStackElements(List<String> techStacks, ArrayList<Object> paramList) {
        StringBuilder techStackElements = new StringBuilder("(");

        for (String techStack : techStacks) {
            techStackElements.append("?");
            paramList.add(techStack);
            if (techStacks.lastIndexOf(techStack) != techStacks.size()-1)
                techStackElements.append(",");
        }

        return techStackElements.append(")").toString();
    }

    private RowMapper<UserProfileForEmployer> userProfileRowMapperForEmployer() {
        return (rs, rowNum) -> UserProfileForEmployer.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .isLike(rs.getBoolean("i_like"))
                .imagePath(rs.getString("image_path"))
                .rank(rs.getString("rank"))
                .introduce(rs.getString("introduce"))
                .developmentArea(rs.getString("development_area"))
                .techStack(rs.getString("tech_stack"))
                .description(rs.getString("description"))
                .build();
    }
}
