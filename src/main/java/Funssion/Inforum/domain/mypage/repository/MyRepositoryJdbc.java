package Funssion.Inforum.domain.mypage.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.Sign;

import Funssion.Inforum.domain.mypage.domain.History;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.member.dto.response.IsProfileSavedDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.exception.HistoryNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;

@Slf4j
@Repository
public class MyRepositoryJdbc implements MyRepository {
    private final JdbcTemplate template;
    public MyRepositoryJdbc(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public List<History> findMonthlyHistoryByUserId(Long userId, Integer year, Integer month) {
        String sql = "select * from member.history where user_id = ? and extract('year' from date) = ? and extract('month' from date) = ? order by date";
        return template.query(sql, historyRowMapper(), userId, year, month);
    }
    public MemberProfileEntity findProfileByUserId(Long userId) {
        String sql = "select name,introduce,tags,image_path from member.user where id = ?";
        return template.queryForObject(sql, MemberProfileEntity.MemberInfoRowMapper(), userId);
    }
    private RowMapper<History> historyRowMapper() {
        return ((rs, rowNum) ->
                History.builder()
                    .id(rs.getLong("id"))
                    .userId(rs.getLong("user_id"))
                    .memoCnt(rs.getLong("memo_cnt"))
                    .blogCnt(rs.getLong("blog_cnt"))
                    .qnaCnt(rs.getLong("qna_cnt"))
                    .date(rs.getDate("date"))
                    .build()
        );
    }

    @Override
    public void updateHistory(Long userId, PostType postType, Sign sign, Date curDate) {
        String fieldName = getFieldName(postType);
        String sql = getSql(sign, fieldName);

        if (template.update(sql, userId, curDate) == 0) throw new HistoryNotFoundException("update fail");
    }

    private String getSql(Sign sign, String fieldName) {
        switch (sign) {
            case PLUS -> {
                return "update member.history set "+fieldName+" = "+fieldName+" + 1 where user_id = ? and date = ?";
            }
            case MINUS -> {
                return "update member.history set "+fieldName+" = "+fieldName+" - 1 where user_id = ? and date = ? and "+fieldName+" > 0";
            }
            default -> {
                return "";
            }
        }
    }

    @Override
    public void createHistory(Long userId, PostType postType) {
        String fieldName = getFieldName(postType);
        String sql = "insert into member.history (user_id, "+fieldName+") values (?, 1)";

        template.update(sql, userId);
    }

    @Override
    public IsProfileSavedDto updateProfile(Long userId, MemberProfileEntity MemberProfileEntity) {
        String sql = "update member.user set introduce = ?, tags = ?, image_path = ? where id = ?";
        int updatedRow = template.update(sql, MemberProfileEntity.getIntroduce(), MemberProfileEntity.getTags(), MemberProfileEntity.getProfileImageFilePath(),userId);
        if (updatedRow ==0) throw new NotFoundException("해당 회원정보를 찾을 수 없습니다");
        return new IsProfileSavedDto(true,MemberProfileEntity.getProfileImageFilePath(),MemberProfileEntity.getTags(),MemberProfileEntity.getIntroduce());
    }

    @Override
    public String findProfileImageNameById(Long userId) {
        String sql = "select image_path from member.user where id =?";
        return template.queryForObject(sql, (rs, rowNum) -> rs.getString("image_path"), userId);
    }

    private static String getFieldName(PostType postType) {
        return postType.toString().toLowerCase() + "_cnt";
    }
}
