package Funssion.Inforum.domain.tag.repository;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.DuplicateException;
import Funssion.Inforum.common.exception.UpdateFailException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.tag.TagUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
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
                saveTagOrUpdateTag(createdMemoId, tagName);
            }catch(DataAccessException e){
                throw new UpdateFailException("Tag 저장중 오류가 발생하였습니다.",e);
            }
        }
        return new IsSuccessResponseDto(true,"tag들이 성공적으로 반영되었습니다.");
    }

    private void saveTagOrUpdateTag(Long createdMemoId, String tagName) {
        int updatedRow = template.update("update tag.info set tag_count = tag_count + 1 where tag_name = ?;", tagName);
        if (updatedRow == 0){
            /**
             * 해당 태그 이름이 테이블에 없으면 태그 정보를 새로 tag table에 추가합니다.
             * 이 때, default로 'is_default' 필드 값은 false로, 태그 참조 횟수는 1로 insert 됩니다.
             * 마찬가지로 tag,memo relation을 가지는 memo_to_tag 테이블에도 해당 relation을 저장합니다.
             */
            Long createdTagId = insertTagIntoTable(tagName);
            insertTagRelationInTable(createdMemoId, createdTagId);
        }
        else if (updatedRow == 1){
            Long updatedTagId = template.queryForObject("select id from tag.info where tag_name = ?", Long.class, tagName);
            insertTagRelationInTable(createdMemoId,updatedTagId);
        }
        else {
            throw new DuplicateException("중복된 태그정보가 태그 테이블에 존재합니다.. ['saveTagOrUpdateTag' 메서드 오류]");
        }

    }


    public IsSuccessResponseDto updateTags(Long memoId,ArrayList<String> updatedTags) throws SQLException {
        List<String> priorTags = TagUtils.createStringListFromArray( template.queryForObject("select tags from memo.info where memo_id = ?", Array.class,memoId));
        comparePriorTagWithUpdateTag(updatedTags, priorTags,memoId);

        return new IsSuccessResponseDto(true,"tag들이 성공적으로 수정되었습니다.");
    }

    public IsSuccessResponseDto deleteTags(Long memoId) throws SQLException {
        List<String> priorTags = TagUtils.createStringListFromArray( template.queryForObject("select tags from memo.info where memo_id = ?;", Array.class,memoId));
        for (String priorTagName : priorTags) {
            Long priorTagId = template.queryForObject("select id from tag.info where tag_name = ?", Long.class, priorTagName);
            subtractTagCount(priorTagName);
            deleteTagInMemoToTagTable(memoId,priorTagId);
        }
        // 블럭단위로 extract 하면 좋을지도.
        return new IsSuccessResponseDto(true,"성공적으로 태그가 삭제 되었습니다.");
    }

    public List<String> findMostUsedTagsByUserTop2(Long userId) {
        String sql = "select t.tag_name " +
                "from memo.info m, tag.memo_to_tag mtt, tag.info t " +
                "where m.author_id = ? and mtt.memo_id = m.memo_id and mtt.tag_id = t.id " +
                "group by t.tag_name " +
                "order by count(1) desc " +
                "limit 2";

        return template.queryForList(sql, String.class ,userId);
    }


    private void comparePriorTagWithUpdateTag(ArrayList<String> updatedTags, List<String> priorTags,Long memoId) {
        removePriorTagsComparingWithNewTags(updatedTags, priorTags, memoId);
        for (String updatedTagName : updatedTags){
            saveTagOrUpdateTag(memoId,updatedTagName);
        }
    }

    private void removePriorTagsComparingWithNewTags(ArrayList<String> updatedTags, List<String> priorTags, Long memoId) {
        // 기존 tag 리스트와 update태그 리스트를 비교하면서
        // 기존 태그가 update태그 리스트에 포함되지 않으면, 기존 태그정보를 테이블에서 삭제
        // 포함되면, 어차피 해당 태그정 보는 테이블에 포함되어있으므로,. update태그 리스트에서 해당 태그를 삭제. (업데이트 필요 x)
        // I/O를 최소화 하기 위함. -> DB I/O가 많이 일어나는 구간임.
        for (String priorTagName : priorTags) {
            if(!updatedTags.contains(priorTagName)){
                Long priorTagIdNotInUpdatedTags = template.queryForObject("select id from tag.info where tag_name = ?", Long.class, priorTagName);
                subtractTagCount(priorTagName);
                deleteTagInMemoToTagTable(memoId,priorTagIdNotInUpdatedTags);
            }else {
                updatedTags.remove(priorTagName);
            }
        }
    }

    private void insertTagRelationInTable(Long createdMemoId, Long createdTagId) {
        template.update("insert into tag.memo_to_tag(memo_id,tag_id) values(?,?)", createdMemoId, createdTagId);
    }

    private Long insertTagIntoTable(String tagName) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement psmt = con.prepareStatement("insert into tag.info (tag_name) values(?);", new String[]{"id"});
            psmt.setString(1, tagName);
            return psmt;
        }, keyHolder);
        Long createdTagId = keyHolder.getKey().longValue();
        return createdTagId;
    }

    private void deleteTagInMemoToTagTable(Long memoId,Long priorTagId) {
        int deletedRow = template.update("delete from tag.memo_to_tag where tag_id = ? and memo_id = ?", priorTagId,memoId);
        if (deletedRow == 0 ){
            throw new NotFoundException("기존 tag id 정보가 존재하지 않습니다.");
        }else if (deletedRow == 2){
            throw new DuplicateException("중복된 tag가 테이블에 존재합니다.");
        }
    }

    private void subtractTagCount(String priorTagName) {
        template.update("update tag.info set tag_count = tag_count - 1 where tag_name = ?", priorTagName);
    }

}
