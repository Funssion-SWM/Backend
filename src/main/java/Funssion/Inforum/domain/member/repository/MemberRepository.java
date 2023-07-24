package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.entity.Member;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

// interface 로 정의된 MemberRepository 메서드를 통해, Non-Social 로그인 멤버와, Social 로그인 멤버 두가지 repository를 구현

public abstract class MemberRepository<T extends Member> {

    protected JdbcTemplate jdbcTemplate;
    protected abstract T save(T member) throws NoSuchAlgorithmException; //Dto_Member가 아니라 상속받은 NonSocialDtoMember를 참조해야함.....
    //jdbc template 이용 예정이므로 jdbc template insert 방법을 익힐것. https://www.springcloud.io/post/2022-06/jdbctemplate-id/#gsc.tab=0

    public abstract Optional<NonSocialMember> findByEmail(String Email);

    public abstract Optional<NonSocialMember> findByName(String Name);
    //Auth table mapper
    protected RowMapper<NonSocialMember> memberAuthRowMapper(){
        return new RowMapper<NonSocialMember>() {
            @Override
            public NonSocialMember mapRow(ResultSet rs, int rowNum) throws SQLException {
                NonSocialMember member = new NonSocialMember();
                member.setAuth_id(rs.getLong("auth_id"));
                return member;
            }
        };
    }

    protected RowMapper<NonSocialMember> memberUserRowMapper(){
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
