package Funssion.Inforum.domain.mypage.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.mypage.dto.MyRecordNumDto;
import Funssion.Inforum.domain.mypage.dto.MyUserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class MyRepositoryJdbc implements MyRepository {
    private JdbcTemplate template;
    public MyRepositoryJdbc(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public List<MyRecordNumDto> findRecordNumByUserId(int userId) {
        String sql = "with rec as (select records as record\n" +
                "from member.history\n" +
                "where user_id = ?), dat as\n" +
                "(select jsonb_array_elements(record) as dates\n" +
                "from rec)\n" +
                "select dates->>'date' as date, dates->>'count' as post_cnt\n" +
                "from dat\n" +
                "order by date";

        return template.query(sql, MyRecordNumDto.myRecordNumRowMapper(), userId);
    }

    @Override
    public Optional<MyUserInfoDto> findUserInfoByUserId(int userId) {
        String sql = "select user_name from member.member_user where user_id = ?";
        return template.query(sql, MyUserInfoDto.myUserInfoDtoRowMapper(), userId).stream().findAny();
    }

    @Override
    public void createHistory(long userId) {
        String sql = "INSERT INTO member.history (user_id) values (?)";
        template.update(sql, userId);
    }

    @Override
    public void updateCreationToHistory(PostType type, int postId, int userId) {
        log.debug("updateCreationToHistory params = {} {} {}", type, postId, userId);

        String sql = "with dat as\n" +
                "(select cast(daily_history->>'count' as int) as count, (daily_history->>'contents')::jsonb as contents, \n" +
                "('{'||index-1||', \"count\"}')::text[] as count_path, ('{'||index-1||', \"contents\"}')::text[] as content_path\n" +
                "from member.history, jsonb_array_elements(records) with ordinality dates(daily_history, index)\n" +
                "where user_id = ? and daily_history->>'date' = current_date::text)\n" +
                "update member.history\n" +
                "set records =\n" +
                "\tcase \n" +
                "\t\twhen records is null\n" +
                "\t\tthen ('[{\"date\": \"'||current_date::text||'\", \"count\": 1, \"contents\" : [{ \"id\": '||?||', \"type\": \"'||?||'\" }] }]')::jsonb\n" +
                "\t\twhen records @> ('[{\"date\": \"'||current_date::text||'\"}]')::jsonb \n" +
                "\t\tthen (select jsonb_set(jsonb_set(records, count_path, (count + 1)::text::jsonb), content_path, contents||('[{ \"id\": '||?||', \"type\": \"'||?||'\" }]')::jsonb)\n" +
                "\t\tfrom dat)\n" +
                "\t\telse records||('[{\"date\": \"'||current_date::text||'\", \"count\": 1, \"contents\" : [{ \"id\": '||?||', \"type\": \"'||?||'\" }] }]')::jsonb\n" +
                "\tend\n" +
                "where user_id = ?";

        int updated = template.update(sql, userId, postId, type.toString(), postId, type.toString(), postId, type.toString(), userId);
        log.debug("updateCreationToHistory rs = {}", updated);
    }

    @Override
    public void updateDeletionToHistory(PostType type, int postId, int userId) {
        log.debug("updateDeletionToHistory params = {} {} {}", type, postId, userId);

        String sql = "with dat as\n" +
                "(select count, count_path, contents, contents_path, cast((content.index-1) as int) as content_path from\n" +
                "(select cast(daily_history->>'count' as int) as count, (daily_history->>'contents')::jsonb as contents, \n" +
                "('{'||index-1||', \"contents\"}')::text[] as contents_path, ('{'||index-1||', \"count\"}')::text[] as count_path, index\n" +
                "from member.history, jsonb_array_elements(records) with ordinality dates(daily_history, index)\n" +
                "where user_id = ?) t, jsonb_array_elements(contents) with ordinality content(info, index)\n" +
                "where info->>'id' = ?::text and info->>'type' = ?::text)\n" +
                "update member.history\n" +
                "set records =(\n" +
                "\t\tselect jsonb_set(jsonb_set(records, count_path, (count - 1)::text::jsonb), contents_path, contents - content_path)\n" +
                "\t\tfrom dat LIMIT 1)\n" +
                "where user_id = ? and exists (select 1 from dat)";

        template.update(sql, userId, postId, type.toString(), userId);
    }
}
