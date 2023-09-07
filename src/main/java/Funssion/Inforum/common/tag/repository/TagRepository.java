package Funssion.Inforum.common.tag.repository;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
@Slf4j
public class TagRepository {
    private final JdbcTemplate template;
    public TagRepository(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    public List<String> getDefaultTags() {
        String sql = "select tag_name from tag.info where is_default = true;";
        return template.queryForList(sql,String.class);
    }

    public IsSuccessResponseDto saveTags(Long createdMemoId,List<String> tags) {
        for (String tagName : tags) {
            try {
                int updatedRow = template.update("update tag.info set tag_count = tag_count + 1 where tag_name = ?;", tagName);
                if (updatedRow != 1){
                    /**
                     * 해당 태그 이름이 테이블에 없으면 태그 정보를 새로 tag table에 추가합니다.
                     * 이 때, default로 'is_default' 필드 값은 false로, 태그 참조 횟수는 1로 insert 됩니다.
                     * 마찬가지로 tag,memo relation을 가지는 memo_to_tag 테이블에도 해당 relation을 저장합니다.
                     */
                    KeyHolder keyHolder = new GeneratedKeyHolder();

                    Long createdTagId = insertTagIntoTable(tagName, keyHolder);
                    insertTagRelationInTable(createdMemoId, createdTagId);
                }
                else{
                    Long updatedTagId = template.queryForObject("select id from tag.info where tag_name = ?", Long.class, tagName);
                    insertTagRelationInTable(createdMemoId,updatedTagId);
                }
            }catch(DataAccessException e){
                return new IsSuccessResponseDto(false,"tag 저장중 db 오류, 오류 메시지 = "+e.getMessage());
            }
        }
        return new IsSuccessResponseDto(true,"tag들이 성공적으로 반영되었습니다.");
    }

    private void insertTagRelationInTable(Long createdMemoId, Long createdTagId) {
        template.update("insert into tag.memo_to_tag(memo_id,tag_id) values(?,?)", createdMemoId, createdTagId);
    }

    private Long insertTagIntoTable(String tagName, KeyHolder keyHolder) {
        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement("insert into tag.info (tag_name) values(?);", new String[]{"id"});
            psmt.setString(1, tagName);
            return psmt;
        }, keyHolder);
        Long createdTagId = keyHolder.getKey().longValue();
        return createdTagId;
    }

//    public IsSuccessResponseDto updateTags(List<String> tags){
//        for (String tagName : tags) {
//            try {
//                int updatedRow = template.query("update tag.info set tag_count = tag_count + 1 where tag_name = ?;", tagName);
//                if (updatedRow != 1){
//                    /**
//                     * 해당 태그 이름이 테이블에 없으면 태그 정보를 새로 tag table에 추가합니다.
//                     * 이 때, default로 'is_default' 필드 값은 false로, 태그 참조 횟수는 1로 insert 됩니다.
//                     */
//                    template.update("insert into tag.info (tag_name) values(?);",tagName);
//                }
//            }catch(DataAccessException e){
//                return new IsSuccessResponseDto(false,"tag 저장중 db 오류, 오류 메시지 = "+e.getMessage());
//            }
//        }
//        return new IsSuccessResponseDto(true,"tag들이 성공적으로 반영되었습니다.");
//    }

}
