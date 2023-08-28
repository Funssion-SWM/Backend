package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.response.IsSuccessResponseDto;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
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
    public SaveMemberResponseDto save(NonSocialMember member) {
        int loginType = member.getLoginType().getValue();
        // USER - AUTH 테이블은 참조관계이므로, 다음과 같이 작성
        SaveMemberResponseDto savedMember = saveMemberInUserTable(member);
        saveMemberInAuthTable(member,savedMember.getId());

        return savedMember;
    }

    @Override
    public Optional<NonSocialMember> findByEmail(String email) {
        String sql ="SELECT ID,EMAIL,NAME FROM MEMBER.USER WHERE EMAIL = ?";
        try{
            NonSocialMember nonSocialMember = jdbcTemplate.queryForObject(sql,memberRowMapper(),email);
            return Optional.of(nonSocialMember);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }


    @Override
    public Optional<NonSocialMember> findByName(String name) {
        String sql ="SELECT ID,EMAIL,NAME FROM m    ember.user WHERE NAME = ?";

        try{
            NonSocialMember nonSocialMember = jdbcTemplate.queryForObject(sql,memberRowMapper(),name);
            return Optional.of(nonSocialMember);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public IsSuccessResponseDto saveSocialMemberNickname(String nickname, Long userId) {
        return null;
    }

    public String findNameById(Long id) {
        String sql = "select name from member.user where id = ?";
        try {
            String name = jdbcTemplate.queryForObject(sql, String.class, id);
            return name;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("user not found");
        }
    }

    private void saveMemberInAuthTable(NonSocialMember member,Long userId) {
        String authSql = "insert into member.auth(user_id,password) values(?,?)";
        KeyHolder authKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con->{
            PreparedStatement auth_psmt = con.prepareStatement(authSql,new String[]{"id"});
            auth_psmt.setLong(1,userId);
            auth_psmt.setString(2,passwordEncoder.encode(member.getUserPw()));
            return auth_psmt;
        },authKeyHolder);
    }

    private SaveMemberResponseDto saveMemberInUserTable(NonSocialMember member) {
        Date createdDate = Date.valueOf(LocalDate.now());
        String name = member.getUserName();
        String email = member.getUserEmail();
        LoginType loginType = member.getLoginType();
        String userSql = "insert into member.user(name,email,login_type,created_date) values(?,?,?,?)";
        KeyHolder userKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con-> {
            PreparedStatement user_psmt = con.prepareStatement(userSql, new String[]{"id"});
            user_psmt.setString(1, name);
            user_psmt.setString(2, email);
            user_psmt.setInt(3, loginType.getValue());
            user_psmt.setDate(4, createdDate);
            return user_psmt;
        },userKeyHolder);
        long savedUserId = userKeyHolder.getKey().longValue();
        return SaveMemberResponseDto.builder()
                .id(savedUserId)
                .name(name)
                .createdDate(createdDate)
                .email(email)
                .loginType(loginType)
                .build();
    }

    private RowMapper<NonSocialMember> memberRowMapper(){
        return new RowMapper<NonSocialMember>() {
            @Override
            public NonSocialMember mapRow(ResultSet rs, int rowNum) throws SQLException {
                NonSocialMember member = NonSocialMember.builder()
                        .userId(rs.getLong("id"))
                        .userName(rs.getString("name"))
                        .userEmail(rs.getString("email"))
                        .build();
                return member;
            }
        };  
    }


}
