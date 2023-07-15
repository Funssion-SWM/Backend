package Funssion.Inforum.domain.member.repository;

import Funssion.Inforum.domain.member.dto.MemberSaveForm;
import Funssion.Inforum.domain.member.entity.Member;
import Funssion.Inforum.domain.member.entity.NonSocialMember;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongToIntFunction;

import static org.junit.jupiter.api.Assertions.*;
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

        MemberSaveForm memberSaveForm = new MemberSaveForm("hi",0,"abcde@gmail.com","123456");
        jdbcInsertMember.withSchemaName("MEMBER").withTableName("USER").usingGeneratedKeyColumns("user_id");
        Map<String, Object> insertMember = new HashMap<>(3);
        insertMember.put("user_name", memberSaveForm.getUser_name());
        insertMember.put("login_type", 0);
        insertMember.put("created_date",LocalDateTime.now());
        Number user_key = jdbcInsertMember.executeAndReturnKey(new MapSqlParameterSource(insertMember));

    }
}