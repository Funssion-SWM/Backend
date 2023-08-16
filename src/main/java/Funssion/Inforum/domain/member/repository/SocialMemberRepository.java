package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.SocialMember;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public class SocialMemberRepository implements MemberRepository<SocialMember> {
    private final JdbcTemplate jdbcTemplate;
    public SocialMemberRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    public SaveMemberResponseDto save(SocialMember member) {
        return saveMemberInUserTable(member);
    }

    @Override
    public Optional<SocialMember> findByEmail(String email){
        String sql ="SELECT ID,NAME,EMAIL,LOGIN_TYPE,CREATED_DATE,IMAGE_PATH,INTRODUCE,TAGS FROM MEMBER.USER WHERE EMAIL = ?";
        try{
            SocialMember socialMember = jdbcTemplate.queryForObject(sql,memberRowMapper(),email);
            return Optional.of(socialMember);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<SocialMember> findByName(String Name) {
        return Optional.empty();
    }
    /* 설정 필요 */

    private SaveMemberResponseDto saveMemberInUserTable(SocialMember member) {
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
    private RowMapper<SocialMember> memberRowMapper(){
        return new RowMapper<SocialMember>() {
            @Override
            public SocialMember mapRow(ResultSet rs, int rowNum) throws SQLException {
                SocialMember member = SocialMember.builder()
                        .userId(rs.getLong("id"))
                        .userEmail(rs.getString("email"))
                        .userName("name")
                        .loginType(LoginType.fromValue(rs.getInt("login_type")))
                        .createdDate(rs.getTimestamp("created_date").toLocalDateTime())
                        .tags(rs.getString("tags"))
                        .introduce(rs.getString("introduce"))
                        .build();
                return member;
            }
        };
    }
}
