package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.entity.NonSocialMember;
import Funssion.Inforum.domain.member.entity.SocialMember;
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
public class SocialMemberRepository implements MemberRepository<SocialMember> {
    @Override
    public SocialMember save(SocialMember member) {
        return null;
    }

    @Override
    public Optional<SocialMember> findByEmail(String Email) {
        return Optional.empty();
    }

    @Override
    public Optional<SocialMember> findByName(String Name) {
        return Optional.empty();
    }
    /* 설정 필요 */
}
