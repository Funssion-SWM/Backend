package Funssion.Inforum.db.repository.member;

import Funssion.Inforum.db.entity.member.Dao_Member;
import Funssion.Inforum.model.member.Dto_Member;
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
import java.util.Map;
import java.util.Optional;

@Repository // 인터페이스 구현체를 바꿀것 같지 않으므로 스프링 빈을 직접 등록하는 것이 아닌, 컴포넌트 스캔방식으로 자동의존관계설정
public class NonSocialMemberRepository implements MemberRepository{
    private final JdbcTemplate jdbcTemplate;

    public NonSocialMemberRepository(DataSource dataSource){ //IDE에서 버그로 dataSource 빨간줄쳐지기도 함
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    //DAO 의 Member 객체로 정의
    public Dto_Member save(Dto_Member member) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(this.jdbcTemplate);
        //----------------- member_user 테이블 insert ----------------//
        jdbcInsert.withTableName("member.user").usingGeneratedKeyColumns("user_id");

        Map<String, Object> userTable = new HashMap<>(3);
        userTable.put("user_name", member.getUser_name());
        userTable.put("login_type", 0);
        LocalDateTime currentDateTime = LocalDateTime.now();
        userTable.put("user_createdAt",currentDateTime);
        //----------------------------------------------------------//

        //---------------- auth_nonsocial 테이블 insert -------------//
        jdbcInsert.withTableName("member.auth_nonsocial").usingGeneratedKeyColumns("user_nonsocial_id");

        Map<String,Object> authTable = new HashMap<>(3);
        authTable.put("user_email", member.getUser_email());
        authTable.put("user_pwd",member.)

        //----------------- auth_nonsocial 테이블 insert -------------//
        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        daoMember.setUser_id(key.longValue());
        return daoMember;
    }

    @Override
    public Optional<Dao_Member> findByEmail(String Email) {
        jdbcTemplate.queryForObject("select ")
    }

    @Override
    public Optional<Dao_Member> findByName(String Name) {
        return Optional.empty();
    }

    private RowMapper<Dao_Member> memberRowMapper(){
        return new RowMapper<Dao_Member>() {
            @Override
            public Dao_Member mapRow(ResultSet rs, int rowNum) throws SQLException {
                Dao_Member daoMember = new Dao_Member();
                daoMember.setUser_id(rs.getLong("user_id"));
                daoMember.setUser_name(rs.getString("user_name"));
                return daoMember;
            }
        }
    }
}
