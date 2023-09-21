package Funssion.Inforum.domain.post.memo.repository;


import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;

import java.util.List;

public interface MemoRepository {

    Memo create(Memo memo);
    List<Memo> findAllByDaysOrderByLikes(Integer days);
    List<Memo> findAllOrderById();
    List<Memo> findAllByUserIdOrderById(Long userId);
    List<Memo> findAllLikedMemosByUserId(Long userId);
    List<Memo> findAllDraftMemosByUserId(Long userId);
    List<Memo> findAllBySearchQuery(List<String> searchStringList, OrderType orderType);
    List<Memo> findAllByTag(String tagText, OrderType orderType);
    List<Memo> findAllByTag(String tagText, Long userId, OrderType orderType);
    Memo findById(Long id);
    Memo updateContentInMemo(MemoSaveDto form, Long memoId);
    Memo updateLikesInMemo(Long likes, Long memoId);
    void updateAuthorProfile(Long authorId, String authorProfileImagePath);
    void delete(Long id);

}
