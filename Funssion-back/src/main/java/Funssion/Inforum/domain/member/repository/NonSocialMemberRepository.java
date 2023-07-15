package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.SHA256;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository // 인터페이스 구현체를 바꿀것 같지 않으므로 스프링 빈을 직접 등록하는 것이 아닌, 컴포넌트 스캔방식으로 자동의존관계설정
public class NonSocialMemberRepository implements MemberRepository<NonSocialMember> {
    private final JdbcTemplate jdbcTemplate;

    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    //DAO 의 Member 객체로 정의
    public NonSocialMember save(NonSocialMember member) throws NoSuchAlgorithmException {
        SimpleJdbcInsert jdbcInsertMember = new SimpleJdbcInsert(this.jdbcTemplate);
        //----------------- member.user 테이블 insert ----------------//
        jdbcInsertMember.withSchemaName("MEMBER").withTableName("MEMBER_USER").usingGeneratedKeyColumns("user_id");
        Map<String, Object> insertMember = new HashMap<>(3);
        insertMember.put("user_name", member.getUser_name());
        insertMember.put("login_type", 0);
        insertMember.put("created_date",LocalDateTime.now());
        Number user_key = jdbcInsertMember.executeAndReturnKey(new MapSqlParameterSource(insertMember));
        log.info("user_key = {}",user_key);
        //----------------------------------------------------------//

        //---------------- member.auth 테이블 insert -------------//
        SHA256 sha256 = new SHA256();
        SimpleJdbcInsert jdbcInsertAuth = new SimpleJdbcInsert(this.jdbcTemplate);
        jdbcInsertAuth.withSchemaName("MEMBER").withTableName("MEMBER_AUTH").usingGeneratedKeyColumns("auth_id");
        Map<String,Object> insertAuth = new HashMap<>(3);
        insertAuth.put("user_email", member.getUser_email());
        insertAuth.put("user_pw",sha256.encrypt(member.getUser_pw()));
        insertAuth.put("user_id",user_key);
        Number nonsocial_key = jdbcInsertAuth.executeAndReturnKey(new MapSqlParameterSource(insertAuth));

        //---------------------------------------------------------//
        NonSocialMember authMember = objectMapper.convertValue(insertAuth, NonSocialMember.class);
        return authMember;
    }

    @Override
    public Optional<NonSocialMember> findByEmail(String Email) {
        List<NonSocialMember> result = this.jdbcTemplate.query("SELECT * FROM MEMBER.MEMBER_AUTH WHERE USER_EMAIL = ?",memberRowMapper(),Email);
        return result.stream().findAny();
    }

    @Override
    public Optional<NonSocialMember> findByName(String Name) {
        List<NonSocialMember> result = this.jdbcTemplate.query("SELECT * FROM MEMBER.MEMBER_USER WHERE USER_NAME = ?",memberRowMapper(),Name);
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
