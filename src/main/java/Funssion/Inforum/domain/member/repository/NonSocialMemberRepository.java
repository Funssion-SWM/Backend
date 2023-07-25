package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.entity.NonSocialMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Repository // 인터페이스 구현체를 바꿀것 같지 않으므로 스프링 빈을 직접 등록하는 것이 아닌, 컴포넌트 스캔방식으로 자동의존관계설정
public class NonSocialMemberRepository implements MemberRepository<NonSocialMember> {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public NonSocialMemberRepository(DataSource dataSource, PasswordEncoder passwordEncoder){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.passwordEncoder = passwordEncoder;
    }
    ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @Override
    //DAO 의 Member 객체로 정의
    public NonSocialMember save(NonSocialMember member) throws NoSuchAlgorithmException {
        String sql = "insert into member.member_user(user_name,login_type,created_date) values(?,?,?)";
        int loginType = member.getLoginType().getValue();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(con-> {
            PreparedStatement psmt = con.prepareStatement(sql, new String[]{"user_id"});
            psmt.setString(1,member.getUserName());
            psmt.setInt(2,loginType);
            psmt.setDate(3, Date.valueOf(LocalDate.now()));
            return psmt;
        },keyHolder);
        long key = keyHolder.getKey().longValue();
        member.setUserId(key);
        return member;
    }

    @Override
    public Optional<NonSocialMember> findByEmail(String email) {
        String sql ="SELECT AUTH_ID,USER_ID,USER_PW,USER_EMAIL FROM MEMBER.MEMBER_AUTH WHERE USER_EMAIL = ?";
        try{
            NonSocialMember nonSocialMember = jdbcTemplate.queryForObject(sql,memberAuthRowMapper(),email);
            return Optional.of(nonSocialMember);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<NonSocialMember> findByName(String name) {
        String sql ="SELECT USER_ID FROM MEMBER.MEMBER_USER WHERE USER_NAME = ?";
        try{
            NonSocialMember nonSocialMember = jdbcTemplate.queryForObject(sql,memberUserRowMapper(),name);
            return Optional.of(nonSocialMember);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    private RowMapper<NonSocialMember> memberAuthRowMapper(){
        return new RowMapper<NonSocialMember>() {
            @Override
            public NonSocialMember mapRow(ResultSet rs, int rowNum) throws SQLException {
                NonSocialMember member = new NonSocialMember();
                member.setUserId(rs.getLong("user_id"));
                member.setAuthId(rs.getLong("auth_id"));
                member.setUserPw(rs.getString("user_pw"));
                member.setUserEmail(rs.getString("user_email"));
                return member;
            }
        };
    }

    private RowMapper<NonSocialMember> memberUserRowMapper(){
        return new RowMapper<NonSocialMember>() {
            @Override
            public NonSocialMember mapRow(ResultSet rs, int rowNum) throws SQLException {
                NonSocialMember member = new NonSocialMember();
                member.setUserId(rs.getLong("user_id"));

                return member;
            }
        };
    }
}
