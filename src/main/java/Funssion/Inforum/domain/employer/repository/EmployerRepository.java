package Funssion.Inforum.domain.employer.repository;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.employer.domain.Employee;
import Funssion.Inforum.domain.employer.dto.EmployerLikesEmployee;
import Funssion.Inforum.domain.employer.dto.EmployerProfile;
import Funssion.Inforum.domain.employer.dto.EmployerUnlikesEmployee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class EmployerRepository {
    private final JdbcTemplate template;
    public EmployerRepository(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }

    public EmployerProfile getEmployerProfile(Long employerId){
        String sql =
                "SELECT id, company, name, image_path,rank " +
                "FROM member.info " +
                "WHERE id = ?";
        return template.queryForObject(sql,EmployerProfileRowMapper(),employerId);
    }

    private RowMapper<EmployerProfile> EmployerProfileRowMapper() {
        return (rs, rowNum) ->
                EmployerProfile.builder()
                        .employerId(rs.getLong("id"))
                        .nickname(rs.getString("name"))
                        .imagePath(rs.getString("image_path"))
                        .companyName(rs.getString("company"))
                        .rank(rs.getString("rank"))
                        .build();
    }
    public List<Employee> getEmployeesLookingForJob(Long page){
        String sql =
                "SELECT MEM.id, MEM.name, MEM.image_path, MEM.rank, MEM.score, " +
                "CASE WHEN EMPLOYER_LIKE.employee_id IS NOT NULL THEN 'true' ELSE 'false' END AS i_like " +
                "FROM member.info MEM " +
                "JOIN (" +
                        "SELECT user_id " +
                        "FROM member.professional_profile " +
                        "WHERE is_visible = true" +
                      ") EMP " +
                "ON MEM.id = EMP.user_id " +
                "LEFT JOIN (" +
                                "SELECT employee_id " +
                                "FROM employer.to_employee " +
                                "WHERE employer_id = ?" +
                           ") EMPLOYER_LIKE " +
                "ON MEM.id = EMPLOYER_LIKE.employee_id " +
                "ORDER BY MEM.score DESC " +
                "LIMIT 5 OFFSET (? * 5)";
        return template.query(sql,employeeListRowMapper(), SecurityContextUtils.getUserId(),page);
    }

    public EmployerLikesEmployee likeEmployee(Long userId){
        Long employerId = SecurityContextUtils.getUserId();
        String sql =
                "INSERT INTO employer.to_employee (employer_id, employee_id) values(?,?)";
        template.update(sql, employerId, userId);
        return new EmployerLikesEmployee(employerId,userId);
    }

    public EmployerUnlikesEmployee unlikeEmployee(Long userId){
        Long employerId = SecurityContextUtils.getUserId();
        String sql =
                "DELETE " +
                "FROM employer.to_employee " +
                "WHERE employer_id = ? and employee_id = ?";
        template.update(sql, employerId, userId);
        return new EmployerUnlikesEmployee(employerId,userId);
    }

    public Boolean doesEmployerLikeEmployee(Long employerId, Long employeeId){
        String sql =
                "SELECT count(employee_id) " +
                "FROM employer.to_employee " +
                "WHERE employer_id = ? and employee_id = ?";
        return template.queryForObject(sql, Long.class,employerId,employeeId).equals(1L) ? true : false;
    }

    private RowMapper<Employee> employeeListRowMapper(){
        return (rs,rowNum) ->
            Employee.builder()
                    .userId(rs.getLong("id"))
                    .username(rs.getString("name"))
                    .imagePath(rs.getString("image_path"))
                    .rank(rs.getString("rank"))
                    .score(rs.getLong("score"))
                    .isLike(rs.getBoolean("i_like"))
                    .build();
    }
}
