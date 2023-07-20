package Funssion.Inforum.mypage.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.memo.dto.MemoListDto;
import Funssion.Inforum.mypage.dto.MyRecordNumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class MyRepositoryJdbc implements MyRepository{
    private final JdbcTemplate template;

    @Override
    public List<MemoListDto> findAllByUserId(int userId) {
        String sql = "select * from memo where user_id = ?";
        List<MemoListDto> memoList = template.query(sql, MemoListDto.memoListRowMapper(), userId);
        if (memoList.isEmpty()) throw new NoSuchElementException("memo not found");
        return memoList;
    }

    @Override
    public List<MyRecordNumDto> findRecordNumByUserId(int userId) {
        String sql = "with rec as (select records as record\n" +
                "from history\n" +
                "where user_id = ?), dat as\n" +
                "(select jsonb_array_elements(record) as dates\n" +
                "from rec)\n" +
                "select dates->>'date' as date, dates->>'count' as postCnt\n" +
                "from dat\n" +
                "order by date";

        return template.query(sql, MyRecordNumDto.myRecordNumRowMapper(), userId);
    }


    @Override
    public void updateHistory(PostType type, int postId, int userId) {
        String sql = "with dat as\n" +
                "(select cast(daily_history->>'count' as int) as count, (daily_history->>'contents')::jsonb as contents, \n" +
                "('{'||index-1||', \"count\"}')::text[] as count_path, ('{'||index-1||', \"contents\"}')::text[] as content_path\n" +
                "from history, jsonb_array_elements(records) with ordinality dates(daily_history, index)\n" +
                "where user_id = ? and daily_history->>'date' = current_date::text)\n" +
                "update history\n" +
                "set records =\n" +
                "\tcase \n" +
                "\t\twhen records is null \n" +
                "\t\tthen ('[{\"date\": \"'||current_date::text||'\", \"count\": 1, \"contents\" : [{ \"id\": '||?||', \"type\": \"'||?||'\" }] }]')::jsonb\n" +
                "\t\twhen records @> ('[{\"date\": \"'||current_date::text||'\"}]')::jsonb \n" +
                "\t\tthen jsonb_set(jsonb_set(records, count_path, (count + 1)::text::jsonb), content_path, contents||('[{ \"id\": '||?||', \"type\": \"'||?||'\" }]')::jsonb)\n" +
                "\t\telse records||('[{\"date\": \"'||current_date::text||'\", \"count\": 1, \"contents\" : [{ \"id\": '||?||', \"type\": \"'||?||'\" }] }]')::jsonb\n" +
                "\tend\n" +
                "from dat\n" +
                "where user_id = ?;\n";
        template.update(sql, userId, postId, type.toString(), postId, type.toString(), postId, type.toString(), userId);
    }
}
