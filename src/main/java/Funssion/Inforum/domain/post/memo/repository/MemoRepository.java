package Funssion.Inforum.domain.post.memo.repository;


import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.request.MemoSaveDto;

import java.util.List;

public interface MemoRepository {

    Memo create(Memo memo);
    List<Memo> findAllByDaysOrderByLikes(Long days);
    List<Memo> findAllOrderById();
    List<Memo> findAllByUserIdOrderById(Long userId);
    Memo findById(Long id);
    Memo updateContentInMemo(MemoSaveDto form, Long memoId);
    Memo updateLikesInMemo(Long likes, Long memoId);
    void updateAuthorProfile(Long authorId, String authorProfileImagePath);
    void delete(Long id);
}
