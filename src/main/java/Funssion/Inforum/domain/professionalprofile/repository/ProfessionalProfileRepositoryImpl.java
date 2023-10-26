package Funssion.Inforum.domain.professionalprofile.repository;

import Funssion.Inforum.common.exception.etc.DeleteFailException;
import Funssion.Inforum.common.exception.etc.UpdateFailException;
import Funssion.Inforum.domain.professionalprofile.domain.ProfessionalProfile;
import Funssion.Inforum.domain.professionalprofile.dto.request.CreateProfessionalProfileDto;
import Funssion.Inforum.domain.professionalprofile.dto.request.UpdatePersonalStatementDto;
import Funssion.Inforum.domain.professionalprofile.dto.request.UpdateResumeDto;
import Funssion.Inforum.domain.tag.TagUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;

@Repository
public class ProfessionalProfileRepositoryImpl implements ProfessionalProfileRepository {

    private final JdbcTemplate template;

    public ProfessionalProfileRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(Long userId, CreateProfessionalProfileDto professionalProfile) {
        String sql = "INSERT INTO member.professional_profile (user_id, introduce, tech_stack, description, answer1, answer2, answer3, resume) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        template.update(sql, getAllParams(userId, professionalProfile));
    }

    @Override
    public ProfessionalProfile findByUserId(Long userId) {
        String sql = "SELECT * " +
                "FROM member.professional_profile " +
                "WHERE user_id = ?";

        return template.queryForObject(sql, professionalPrifileRowMapper(), userId);
    }

    @Override
    public void updatePersonalStatement(Long userId, UpdatePersonalStatementDto personalStatementDto) {
        String sql = "UPDATE member.professional_profile " +
                "SET introduce = ?, tech_stack = ?, description = ?, answer1 = ?, answer2 = ?, answer3 = ? " +
                "WHERE user_id = ?";

        int updatedRows = template.update(sql,getAllParams(personalStatementDto, userId));
        if (updatedRows != 1)
            throw new UpdateFailException("professional_profile updated rows not 1 actually " + updatedRows);
    }

    @Override
    public void updateResume(Long userId, UpdateResumeDto resumeDto) {
        String sql = "UPDATE member.professional_profile " +
                "SET resume = ? " +
                "WHERE user_id = ?";

        int updatedRows = template.update(sql, resumeDto.getResume(), userId);
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

    private Object[] getAllParams(UpdatePersonalStatementDto personalStatementDto ,Long userId) {
        ArrayList<Object> paramList = new ArrayList<>();
        paramList.add(personalStatementDto.getIntroduce());
        paramList.add(personalStatementDto.getTechStack());
        paramList.add(personalStatementDto.getDescription());
        paramList.add(personalStatementDto.getAnswer1());
        paramList.add(personalStatementDto.getAnswer2());
        paramList.add(personalStatementDto.getAnswer3());
        paramList.add(userId);
        return paramList.toArray();
    }

    private Object[] getAllParams(Long userId, CreateProfessionalProfileDto profile) {
        ArrayList<Object> paramList = new ArrayList<>();
        paramList.add(userId);
        paramList.add(profile.getIntroduce());
        paramList.add(profile.getTechStack());
        paramList.add(profile.getDescription());
        paramList.add(profile.getAnswer1());
        paramList.add(profile.getAnswer2());
        paramList.add(profile.getAnswer3());
        paramList.add(profile.getResume());
        return paramList.toArray();
    }

    private RowMapper<ProfessionalProfile> professionalPrifileRowMapper() {
        return (rs, rowNum) -> ProfessionalProfile.builder()
                .userId(rs.getLong("user_id"))
                .introduce(rs.getString("introduce"))
                .techStack(rs.getString("tech_stack"))
                .description(rs.getString("description"))
                .answer1(rs.getString("answer1"))
                .answer2(rs.getString("answer2"))
                .answer3(rs.getString("answer3"))
                .resume(rs.getString("resume"))
                .build();
    }
}
