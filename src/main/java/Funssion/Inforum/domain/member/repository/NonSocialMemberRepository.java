package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.entity.NonSocialMember;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository // 인터페이스 구현체를 바꿀것 같지 않으므로 스프링 빈을 직접 등록하는 것이 아닌, 컴포넌트 스캔방식으로 자동의존관계설정
public class NonSocialMemberRepository implements MemberRepository<NonSocialMember> {
    private final JdbcTemplate jdbcTemplate;

    public NonSocialMemberRepository(DataSource dataSource){ //IDE에서 버그로 dataSource 빨간줄쳐지기도 함
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    //DAO 의 Member 객체로 정의
    public NonSocialMember save(NonSocialMember member) {
        SimpleJdbcInsert jdbcInsertUser = new SimpleJdbcInsert(this.jdbcTemplate);
        //----------------- member_user 테이블 insert ----------------//
        jdbcInsertUser.withSchemaName("MEMBER").withTableName("USER").usingGeneratedKeyColumns("user_id");
        Map<String, Object> userTable = new HashMap<>(3);
        userTable.put("user_name", member.getUser_name());
        userTable.put("login_type", 0);
        LocalDateTime currentDateTime = LocalDateTime.now();
        userTable.put("user_createdAt",currentDateTime);
        System.out.println(member.getUser_name()+member.getUser_email()+member.getUser_email());
        Number user_key = jdbcInsertUser.executeAndReturnKey(new MapSqlParameterSource(userTable));
        member.setUser_id(user_key.longValue());

        //----------------------------------------------------------//

        //---------------- auth_nonsocial 테이블 insert -------------//
        SimpleJdbcInsert jdbcInsertNonSocialUser = new SimpleJdbcInsert(this.jdbcTemplate);
        jdbcInsertNonSocialUser.withSchemaName("MEMBER").withTableName("nonsocialuser").usingGeneratedKeyColumns("user_email_id");

        Map<String,Object> authTable = new HashMap<>(3);
        authTable.put("user_email", member.getUser_email());
        authTable.put("user_pwd",member.getUser_pwd());
        System.out.println("user key"+user_key);
        authTable.put("user_id",user_key);
        //---------------------------------------------------------//
        Number nonsocial_key = jdbcInsertNonSocialUser.executeAndReturnKey(new MapSqlParameterSource(authTable));
        return member;
    }

    @Override
    public Optional<NonSocialMember> findByEmail(String Email) {
        List<NonSocialMember> result = this.jdbcTemplate.query("SELECT * FROM MEMBER.NONSOCIALUSER WHERE USER_EMAIL = ?",memberRowMapper(),Email);
        return result.stream().findAny();
    }

    @Override
    public Optional<NonSocialMember> findByName(String Name) {
        List<NonSocialMember> result = this.jdbcTemplate.query("SELECT * FROM MEMBER.USER WHERE USER_NAME = ?",memberRowMapper(),Name);
        return result.stream().findAny();
    }

    private RowMapper<NonSocialMember> memberRowMapper(){
        return new RowMapper<NonSocialMember>() {
            @Override
            public NonSocialMember mapRow(ResultSet rs, int rowNum) throws SQLException {
                NonSocialMember member = new NonSocialMember();
                member.setUser_id(rs.getLong("user_id"));
                return member;
            }
        };
    }
}
