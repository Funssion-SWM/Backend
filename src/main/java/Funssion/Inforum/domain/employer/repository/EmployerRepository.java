package Funssion.Inforum.domain.employer.repository;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.employer.domain.Employee;
import Funssion.Inforum.domain.employer.domain.EmployeeWithStatus;
import Funssion.Inforum.domain.employer.domain.InterviewResult;
import Funssion.Inforum.domain.employer.dto.EmployerLikesEmployee;
import Funssion.Inforum.domain.employer.dto.EmployerProfile;
import Funssion.Inforum.domain.employer.dto.EmployerUnlikesEmployee;
import Funssion.Inforum.domain.interview.constant.InterviewStatus;
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

    public List<Employee> getInterviewEmployees(Boolean isDone){
        Long employerId = SecurityContextUtils.getAuthorizedUserId();
        String sql =
                "SELECT U.id, U.name, U.image_path, U.rank, EMP.introduce, EMP.development_area, EMP.description, EMP.tech_stack, EMP.is_visible " +
                "FROM member.info U, member.professional_profile EMP " +
                "WHERE U.id = EMP.user_id " +
                "AND (SELECT status " +
                     "FROM interview.info " +
                     "WHERE employer_id = ? AND employee_id = U.id) ";
        sql += isDone
                ? " = "
                : " != " ;
        sql += "'DONE'";
        return template.query(sql,employeeListRowMapper(),employerId);
    }


    public List<EmployeeWithStatus> getLikeEmployees() {
        Long employerId = SecurityContextUtils.getAuthorizedUserId();
        String sql =
                "SELECT U.id, U.name, U.image_path, U.rank, EMP.introduce, EMP.development_area, EMP.description, EMP.tech_stack, EMP.is_visible, INTER.status " +
                "FROM member.info U " +
                "INNER JOIN member.professional_profile EMP " +
                "ON U.id = EMP.user_id " +
                "LEFT JOIN interview.info INTER " +
                "ON INTER.employee_id = U.id " +
                "INNER JOIN employer.to_employee L " +
                "ON L.employer_id = ? and L.employee_id = U.id ";
        return template.query(sql,employeeWithStatusListRowMapper(),employerId);
    }

    public InterviewResult getInterviewResultOf(Long userId){
        Long employerId = SecurityContextUtils.getAuthorizedUserId();
        String sql =
                "SELECT question_1, answer_1, question_2, answer_2, question_3, answer_3 " +
                "FROM interview.info " +
                "WHERE employee_id = ? and employer_id = ?";
        return template.queryForObject(sql, interviewReulstRowMapper(),userId,employerId);
    }

    private RowMapper<InterviewResult> interviewReulstRowMapper() {
        return (rs,rowNum) ->
            InterviewResult.builder()
                    .question1(rs.getString("question_1"))
                    .answer1(rs.getString("answer_1"))
                    .question2(rs.getString("question_2"))
                    .answer2(rs.getString("answer_2"))
                    .question3(rs.getString("question_3"))
                    .answer3(rs.getString("answer_3"))
                    .build();
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


    public EmployerLikesEmployee likeEmployee(Long userId){
        Long employerId = SecurityContextUtils.getAuthorizedUserId();
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
                    .developmentArea(rs.getString("development_area"))
                    .introduce(rs.getString("introduce"))
                    .techStack(rs.getString("tech_stack"))
                    .description(rs.getString("description"))
                    .isVisible(rs.getBoolean("is_visible"))
                    .build();
    }

    private RowMapper<EmployeeWithStatus> employeeWithStatusListRowMapper(){
        return (rs,rowNum) ->
                EmployeeWithStatus.builder()
                        .userId(rs.getLong("id"))
                        .username(rs.getString("name"))
                        .imagePath(rs.getString("image_path"))
                        .rank(rs.getString("rank"))
                        .developmentArea(rs.getString("development_area"))
                        .introduce(rs.getString("introduce"))
                        .techStack(rs.getString("tech_stack"))
                        .description(rs.getString("description"))
                        .isVisible(rs.getBoolean("is_visible"))
                        .status(InterviewStatus.nullableValueOf(rs.getString("status")))
                        .build();
    }
}
