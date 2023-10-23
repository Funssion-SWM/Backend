package Funssion.Inforum.domain.post.memo.repository;


import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;

import java.util.List;

public interface MemoRepository {

    Memo create(Memo memo);
    List<Memo> findAllByDaysOrderByLikes(DateType period, Long pageNum, Long resultCntPerPage);
    List<Memo> findAllOrderById(Long pageNum, Long resultCntPerPage);
    List<Memo> findAllByUserIdOrderById(Long userId, Long pageNum, Long resultCntPerPage);
    List<Memo> findAllLikedMemosByUserId(Long userId, Long pageNum, Long resultCntPerPage);
    List<Memo> findAllDraftMemosByUserId(Long userId);
    List<Memo> findAllBySearchQuery(List<String> searchStringList, OrderType orderType, Long userId, Long pageNum, Long resultCntPerPage);
    List<Memo> findAllByTag(String tagText, OrderType orderType, Long pageNum, Long resultCntPerPage);
    List<Memo> findAllByTag(String tagText, Long userId, OrderType orderType, Long pageNum, Long resultCntPerPage);
    List<Memo> findAllBySeriesId(Long seriesId);
    List<String> findTop3ColorsBySeriesId(Long seriesId);
    Memo findById(Long id);
    Memo updateContentInMemo(MemoSaveDto form, Long memoId);
    Memo updateContentInMemo(MemoSaveDto form, Long memoId, Boolean isCreated);
    Memo updateLikesInMemo(Long memoId, Sign sign);
    void updateAuthorProfile(Long authorId, String authorProfileImagePath);
    void updateSeriesIdAndTitle(Long seriesId, String seriesTitle, Long authorId, List<Long> memoIdList);
    void updateSeriesIdsToZero(Long seriesId, Long authorId);
    void delete(Long id);
    void updateQuestionsCountOfMemo(Long memoId, Sign sign);

}
