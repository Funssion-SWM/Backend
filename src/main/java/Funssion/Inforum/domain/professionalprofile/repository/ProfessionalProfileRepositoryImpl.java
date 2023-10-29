package Funssion.Inforum.domain.professionalprofile.repository;

import Funssion.Inforum.common.exception.etc.DeleteFailException;
import Funssion.Inforum.common.exception.etc.UpdateFailException;
import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import Funssion.Inforum.domain.professionalprofile.dto.request.SaveProfessionalProfileDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;

@Repository
public class ProfessionalProfileRepositoryImpl implements ProfessionalProfileRepository {

    private final JdbcTemplate template;

    public ProfessionalProfileRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(Long userId, SaveProfessionalProfileDto professionalProfile) {
        String sql = "INSERT INTO member.professional_profile (user_id, introduce, development_area, tech_stack, description, answer1, answer2, answer3, resume) " +
                "VALUES (?, ?, ?, ?::jsonb, ?, ?, ?, ?, ?);";

        ArrayList<Object> paramList = new ArrayList<>();
        paramList.add(userId);

        template.update(sql, getAllParams(professionalProfile, paramList).toArray());
    }

    @Override
    public ProfessionalProfile findByUserId(Long userId) {
        String sql = "SELECT * " +
                "FROM member.professional_profile " +
                "WHERE user_id = ?";

        return template.queryForObject(sql, professionalPrifileRowMapper(), userId);
    }

    @Override
    public Boolean findVisibilityByUserId(Long userId) {
        String sql = "SELECT is_visible " +
                "FROM member.professional_profile " +
                "WHERE user_id = ?";

        return template.queryForObject(sql, Boolean.class, userId);
    }

    @Override
    public void update(Long userId, SaveProfessionalProfileDto professionalProfile) {
        String sql = "UPDATE member.professional_profile " +
                "SET introduce = ?, development_area = ?, tech_stack = ?::jsonb, description = ?, answer1 = ?, answer2 = ?, answer3 = ?, resume = ? " +
                "WHERE user_id = ?";

        ArrayList<Object> paramList = getAllParams(professionalProfile, new ArrayList<>());
        paramList.add(userId);

        int updatedRows = template.update(sql,paramList.toArray());
        if (updatedRows != 1)
            throw new UpdateFailException("professional_profile updated rows not 1 actually " + updatedRows);
    }

    @Override
    public void updateVisibility(Long userId, Boolean isVisible) {
        String sql = "UPDATE member.professional_profile " +
                "SET is_visible = ? " +
                "WHERE user_id = ?";

        int updatedRows = template.update(sql, isVisible, userId);
        if (updatedRows != 1)
            throw new UpdateFailException("professional_profile updated rows not 1 actually " + updatedRows);
    }

    @Override
    public void delete(Long userId) {
        String sql = "DELETE FROM member.professional_profile " +
                "WHERE user_id = ?";

        int deletedRows = template.update(sql, userId);

        if (deletedRows != 1)
            throw new DeleteFailException("professional_profile deleted rows not 1 actually " + deletedRows);
    }

    private ArrayList<Object> getAllParams(SaveProfessionalProfileDto profile, ArrayList<Object> paramList) {
        paramList.add(profile.getIntroduce());
        paramList.add(profile.getDevelopmentArea());
        paramList.add(profile.getTechStack());
        paramList.add(profile.getDescription());
        paramList.add(profile.getAnswer1());
        paramList.add(profile.getAnswer2());
        paramList.add(profile.getAnswer3());
        paramList.add(profile.getResume());
        return paramList;
    }

    private RowMapper<ProfessionalProfile> professionalPrifileRowMapper() {
        return (rs, rowNum) -> ProfessionalProfile.builder()
                .userId(rs.getLong("user_id"))
                .introduce(rs.getString("introduce"))
                .developmentArea(rs.getString("development_area"))
                .techStack(rs.getString("tech_stack"))
                .description(rs.getString("description"))
                .answer1(rs.getString("answer1"))
                .answer2(rs.getString("answer2"))
                .answer3(rs.getString("answer3"))
                .resume(rs.getString("resume"))
                .isVisible(rs.getBoolean("is_visible"))
                .build();
    }
}
