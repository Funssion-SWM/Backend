package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.entity.NonSocialMember;
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

    @Transactional
    @Override
    //DAO 의 Member 객체로 정의
    public NonSocialMember save(NonSocialMember member) throws NoSuchAlgorithmException {
        //----------------- member.user 테이블 insert -----------------//
        String userSql = "insert into member.member_user(user_name,login_type,created_date) values(?,?,?)";
        int loginType = member.getLoginType().getValue();
        KeyHolder userKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con-> {
            PreparedStatement user_psmt = con.prepareStatement(userSql, new String[]{"user_id"});
            user_psmt.setString(1,member.getUserName());
            user_psmt.setInt(2,loginType);
            user_psmt.setDate(3, Date.valueOf(LocalDate.now()));
            return user_psmt;
        },userKeyHolder);
        long key = userKeyHolder.getKey().longValue();
        member.setUserId(key);

        //----------------- member.auth 테이블 insert -----------------//
        String authSql = "insert into member.member_auth(user_id,user_email,user_pw) values(?,?,?)";
        KeyHolder authKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con->{
            PreparedStatement auth_psmt = con.prepareStatement(authSql,new String[]{"auth_id"});
            auth_psmt.setLong(1,key);
            auth_psmt.setString(2,member.getUserEmail());
            auth_psmt.setString(3,passwordEncoder.encode(member.getUserPw()));
            return auth_psmt;
        },authKeyHolder);
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
