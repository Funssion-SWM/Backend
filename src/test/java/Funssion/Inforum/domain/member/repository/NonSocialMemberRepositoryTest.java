package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.dto.NonSocialMemberSaveForm;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
class NonSocialMemberRepositoryTest {

    private final NonSocialMemberRepository nonSocialMemberRepository;
    private final JdbcTemplate jdbcTemplate;

    SimpleJdbcInsert jdbcInsertMember;
    @BeforeEach
    void beforeEach(){
        jdbcInsertMember = new SimpleJdbcInsert(this.jdbcTemplate);
    }

    @Test
    void checkDuplicateName(){

        NonSocialMemberSaveForm nonSocialMemberSaveForm = new NonSocialMemberSaveForm("hi",0,"abcde@gmail.com","123456");
        jdbcInsertMember.withSchemaName("MEMBER").withTableName("USER").usingGeneratedKeyColumns("user_id");
        Map<String, Object> insertMember = new HashMap<>(3);
        insertMember.put("user_name", nonSocialMemberSaveForm.getUser_name());
        insertMember.put("login_type", 0);
        insertMember.put("created_date",LocalDateTime.now());
        Number user_key = jdbcInsertMember.executeAndReturnKey(new MapSqlParameterSource(insertMember));

    }
}