package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.DuplicateException;
import Funssion.Inforum.common.exception.UpdateFailException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.member.constant.LoginType;
import Funssion.Inforum.domain.member.dto.response.SaveMemberResponseDto;
import Funssion.Inforum.domain.member.entity.Member;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.entity.SocialMember;
import Funssion.Inforum.domain.post.memo.dto.request.PasswordUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Repository // 인터페이스 구현체를 바꿀것 같지 않으므로 스프링 빈을 직접 등록하는 것이 아닌, 컴포넌트 스캔방식으로 자동의존관계설정
public class MemberRepositoryImpl implements MemberRepository {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public MemberRepositoryImpl(DataSource dataSource, PasswordEncoder passwordEncoder){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    //DAO 의 Member 객체로 정의
    public SaveMemberResponseDto save(NonSocialMember member) {
        // USER - AUTH 테이블은 참조관계이므로, 다음과 같이 작성
        SaveMemberResponseDto savedMember = saveNonSocialMemberInUserTable(member);
        saveMemberInAuthTable(member,savedMember.getId());

        return savedMember;
    }

    public SaveMemberResponseDto save(SocialMember member) {
        SaveMemberResponseDto savedMember = saveSocialMemberInUserTable(member);

        return savedMember;
    }

    public Optional<NonSocialMember> findNonSocialMemberByEmail(String email) {
        String sql ="SELECT A.ID AS A_ID ,U.ID AS U_ID,A.PASSWORD,U.EMAIL FROM member.info AS U JOIN MEMBER.AUTH AS A ON U.ID = A.USER_ID WHERE U.EMAIL = ?";
        try{
            NonSocialMember nonSocialMember = jdbcTemplate.queryForObject(sql,nonSocialmemberRowMapper(),email);
            return Optional.of(nonSocialMember);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }
    public Optional<SocialMember> findSocialMemberByEmail(String email){
        String sql ="SELECT ID,NAME,EMAIL,LOGIN_TYPE,CREATED_DATE,IMAGE_PATH,INTRODUCE,TAGS FROM member.info WHERE EMAIL = ?";
        try{
            SocialMember socialMember = jdbcTemplate.queryForObject(sql,socialMemberRowMapper(),email);
            return Optional.of(socialMember);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }


    @Override
    public Optional<Member> findByName(String name) {
        String sql ="SELECT ID,EMAIL,NAME FROM member.info WHERE NAME = ?";

        try{
            Member member = jdbcTemplate.queryForObject(sql,memberEmailAndNameRowMapper(),name);
            return Optional.of(member);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    public IsSuccessResponseDto saveSocialMemberNickname(String nickname,Long userId){
        String sql ="UPDATE member.info SET name = ? WHERE id = ?";
        int updatedRow = jdbcTemplate.update(sql, nickname, userId);
        if (updatedRow == 0) {
            throw new NotFoundException("해당 회원정보를 찾을 수 없습니다");
        }
        return new IsSuccessResponseDto(true,"정상적으로 닉네임이 등록되었습니다.");

    }

    @Override
    public String findEmailByNickname(String nickname) {
        String sql ="select email from member.info where name = ?";
        try{
            return jdbcTemplate.queryForObject(sql, String.class, nickname);
        }catch(EmptyResultDataAccessException e){
            throw new NotFoundException("요청하신 닉네임정보로 등록된 이메일이 존재하지 않습니다.");
        }
    }

    @Override
    public IsSuccessResponseDto findAndChangePassword(PasswordUpdateDto passwordUpdateDto, String email) {
        String sql = "update member.auth as auth set password = ? from member.info as memberInfo where memberInfo.email = ?";
        int updatedRow = jdbcTemplate.update(sql, passwordEncoder.encode(passwordUpdateDto.getUserPw()), email);
        if (updatedRow == 0) throw new UpdateFailException("비밀번호가 수정되지 않았습니다.");
        return new IsSuccessResponseDto(true, "비밀번호가 수정되었습니다.");
    }

    @Override
    public String findEmailByAuthCode(String usersTemporaryCode) {
        String sql = "select email from member.auth_code where code = ? and expiration = false";
        try{
            return jdbcTemplate.queryForObject(sql,String.class,usersTemporaryCode);
        }catch (EmptyResultDataAccessException e){
            throw new NotFoundException("이미 만료된 이메일 인증 링크입니다.");
        }catch (IncorrectResultSizeDataAccessException e){
            throw new DuplicateException("중복 링크가 존재합니다. 다시 시도해 주세요");
        }
    }

    public String findNameById(Long id) {
        String sql = "select name from member.info where id = ?";
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
    private SaveMemberResponseDto saveNonSocialMemberInUserTable(NonSocialMember member) {
        LocalDateTime createdDate = LocalDateTime.now();
        String name = member.getUserName();
        String email = member.getUserEmail();
        LoginType loginType = member.getLoginType();
        String userSql = "insert into member.info(name,email,login_type,created_date) values(?,?,?,?)";
        KeyHolder userKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con-> {
            PreparedStatement user_psmt = con.prepareStatement(userSql, new String[]{"id"});
            user_psmt.setString(1, name);
            user_psmt.setString(2, email);
            user_psmt.setInt(3, loginType.getValue());
            user_psmt.setTimestamp(4, Timestamp.valueOf(createdDate));
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

    private SaveMemberResponseDto saveSocialMemberInUserTable(SocialMember member) {
        LocalDateTime createdDate = LocalDateTime.now();
        String name = member.getUserName();
        String email = member.getUserEmail();
        LoginType loginType = member.getLoginType();
        String userSql = "insert into member.info(name,email,login_type,created_date) values(?,?,?,?)";
        KeyHolder userKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con-> {
            PreparedStatement user_psmt = con.prepareStatement(userSql, new String[]{"id"});
            user_psmt.setString(1, name);
            user_psmt.setString(2, email);
            user_psmt.setInt(3, loginType.getValue());
            user_psmt.setTimestamp(4, Timestamp.valueOf(createdDate));
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

    private RowMapper<NonSocialMember> nonSocialmemberRowMapper(){
        return new RowMapper<NonSocialMember>() {
            @Override
            public NonSocialMember mapRow(ResultSet rs, int rowNum) throws SQLException {
                NonSocialMember member = NonSocialMember.builder()
                        .userId(rs.getLong("u_id"))
                        .authId(rs.getLong("a_id"))
                        .userPw(rs.getString("password"))
                        .userEmail(rs.getString("email"))
                        .build();
                return member;
            }
        };
    }
    private RowMapper<SocialMember> socialMemberRowMapper(){
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


    private RowMapper<Member> memberEmailAndNameRowMapper(){
        return new RowMapper<Member>() {
            @Override
            public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
                Member member = Member.builder()
                        .userId(rs.getLong("id"))
                        .userEmail(rs.getString("email"))
                        .userName(rs.getString("name"))
                        .build();
                return member;
            }
        };
    }


}
