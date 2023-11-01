package Funssion.Inforum.domain.interview.repository;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.interview.constant.InterviewStatus;
import Funssion.Inforum.domain.interview.domain.Interview;
import Funssion.Inforum.domain.interview.dto.InterviewAnswerDto;
import Funssion.Inforum.domain.interview.dto.QuestionsDto;
import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class InterviewRepository {
    private final JdbcTemplate template;
    public InterviewRepository(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }

    public void saveQuestions(Long employeeId, QuestionsDto questionsDto){
        Long employerId = SecurityContextUtils.getAuthorizedUserId();
        String sql = "INSERT INTO interview.info (employer_id, employee_id, question_1, question_2, question_3) values(?,?,?,?,?)";
        template.update(sql,employerId,employeeId, questionsDto.getQuestion1(),questionsDto.getQuestion2(),questionsDto.getQuestion3());
    }

    public boolean findIfAlreadyInterviewing(Long employeeId){
        Long employerId = SecurityContextUtils.getAuthorizedUserId();
        String sql =
                "SELECT EXISTS " +
                    "(SELECT 1 " +
                    "FROM interview.info " +
                    "WHERE employer_id = ? AND employee_id = ? AND interviewing NOT IN ('DONE'))";
        return template.queryForObject(sql,Boolean.class,employerId,employeeId);
    }
    public Interview getInterviewQuestionOf(Long employeeId){
        String sql =
                "SELECT employer_id, status, question1, question2, question3 " +
                "FROM interview.info " +
                "WHERE employee_id = ?";
        return template.queryForObject(sql,interviewRowMapper(),employeeId);
    }

    public InterviewStatus saveAnswerOfQuestion(InterviewAnswerDto interviewAnswerDto,Long userId) {
        Status status = switch (interviewAnswerDto.getQuestionNumber()) {
            case 1 -> new Status("answer_1", InterviewStatus.ING_Q2 );
            case 2 -> new Status("answer_2", InterviewStatus.ING_Q3 );
            case 3 -> new Status("answer_3", InterviewStatus.DONE );
            default -> throw new BadRequestException("인터뷰 답변객체의 번호가 '1','2','3' 이 아닙니다.");
        };

        String sql =
                "UPDATE interview.info " +
                "SET " + status.getColumnNameOfAnswer() + "= ? AND status = ? " +
                "WHERE employer_id = ? and employee_id = ?";

        template.update(sql,interviewAnswerDto.getAnswer(), status.getInterviewStatusAfter(), interviewAnswerDto.getEmployerId(),userId);

        return getInterviewStatusOfUser(interviewAnswerDto.getEmployerId(), userId);
    }
    public InterviewStatus startInterview(Long employerId, Long employeeId) {
        String sql =
                "UPDATE interview.info " +
                "SET status = ? " +
                "WHERE employer_id = ? and employee_id = ?";
        template.update(sql, InterviewStatus.ING_Q1,employerId,employeeId);

        return getInterviewStatusOfUser(employerId,employeeId);
    }

    public InterviewStatus getInterviewStatusOfUser(Long employerId, Long userId){
        String sql =
                "SELECT status " +
                "FROM interview.info " +
                "WHERE employer_id = ? and employee_id = ?";
        return InterviewStatus.valueOf(template.queryForObject(sql,String.class, employerId, userId));
    }
    public Boolean isAuthorizedInterview(Long employerId,Long employeeId) {
        String sql =
                "SELECT EXISTS(" +
                        "SELECT employee_id " +
                        "FROM interview.info " +
                        "WHERE employer_id = ? AND employee_id = ?)";
        return template.queryForObject(sql, Boolean.class, employerId,employeeId);
    }
    private RowMapper<Interview> interviewRowMapper(){
        return (rs,rowNum)->
            Interview.builder()
                    .employerId(rs.getLong("employer_id"))
                    .status(rs.getString("status"))
                    .question1(rs.getString("question1"))
                    .question2(rs.getString("question2"))
                    .question3(rs.getString("question3"))
                    .build();
    }

    public boolean isMismatchWithStatus(InterviewAnswerDto interviewAnswerDto, Long userId) {
        InterviewStatus interviewStatusOfUser = getInterviewStatusOfUser(interviewAnswerDto.getEmployerId(), userId);
        if (isStatusMismatchWithQuestionNumber(interviewAnswerDto, interviewStatusOfUser)) return true;
        return false;
    }

    private static boolean isStatusMismatchWithQuestionNumber(InterviewAnswerDto interviewAnswerDto, InterviewStatus interviewStatusOfUser) {
        return !interviewStatusOfUser.getStatus().startsWith(String.valueOf(interviewAnswerDto.getQuestionNumber()));
    }


    @Getter
    private class Status{
        private String columnNameOfAnswer;
        private InterviewStatus interviewStatusAfter;

        public Status(String columnNameOfAnswer, InterviewStatus interviewStatusAfterAnswer) {
            this.columnNameOfAnswer = columnNameOfAnswer;
            this.interviewStatusAfter = interviewStatusAfterAnswer;
        }
    }

}
