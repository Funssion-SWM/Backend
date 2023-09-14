package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.dto.request.CodeCheckDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Repository
public class AuthCodeRepository {
    private final JdbcTemplate jdbcTemplate;

    /*
     * 1. 이메일 인증코드 전송을 이전에 시행한 적이 있고, 만료되지 않았다면 해당 row의 expiration 필드값을 true로 변환
     */
    public void invalidateExistedEmailCode(String beVerifiedEmail){
        log.info("Email ={} Verification Occurred",beVerifiedEmail);
        String sql =
                "UPDATE MEMBER.AUTH_CODE " +
                        "SET EXPIRATION = true " +
                        "WHERE EMAIL = ? AND EXPIRATION = false";
        try {
            jdbcTemplate.update(sql, beVerifiedEmail);
        }catch(DataAccessException e){
            log.error("error in invalidateExistedEmailCode = "+e.getMessage());
        }

    }

    public void insertEmailCodeForVerification(String beVerifiedEmail,String code){
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(5); //유효시간 5분

        //----------------- member.info 테이블 insert -----------------//
        String sql = "insert into member.auth_code(email,code,due_date) values(?,?,?)";
        KeyHolder userKeyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(con -> {
                PreparedStatement psmt = con.prepareStatement(sql, new String[]{"id"});
                psmt.setString(1, beVerifiedEmail);
                psmt.setString(2, code);
                psmt.setTimestamp(3, Timestamp.valueOf(dueDate));
                return psmt;
            }, userKeyHolder);
        }catch(DataAccessException e){
            log.error("error in insert= {}"+e.getMessage());
        }
    }

    public boolean checkRequestCode(CodeCheckDto requestCodeDto) {
        String sql = "SELECT email, code FROM member.auth_code WHERE email = ? AND NOT expiration AND extract('EPOCH' from (due_date - now())) between 0 and 300";
        try {
            CodeCheckDto rightCodeDto = jdbcTemplate.queryForObject(sql, codeCheckDtoRowMapper(), requestCodeDto.getEmail());
            return requestCodeDto.equals(rightCodeDto);
        } catch (EmptyResultDataAccessException e) {
            return false; //조건에 부합하는 어떠한 row 도 존재하지 않음
        }
    }
    public void removeExpiredEmailCode(){
        String sql = "delete from member.auth_code where expiration or extract('EPOCH' from (now() - due_date)) > 300";
        try{
            jdbcTemplate.update(sql);
        }catch(DataAccessException e){
            log.error("removeExpiredEmailCode 스케쥴링 실패 = {}",e.getMessage());
        }
    }

    private RowMapper<CodeCheckDto> codeCheckDtoRowMapper(){
        return new RowMapper<CodeCheckDto>(){
            @Override
            public CodeCheckDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                CodeCheckDto codeCheckDto = CodeCheckDto.builder()
                        .email(rs.getString("email"))
                        .code(rs.getString("code"))
                        .build();
                return codeCheckDto;
            }
        };
    }
}
