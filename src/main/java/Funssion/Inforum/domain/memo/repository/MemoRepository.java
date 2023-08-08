package Funssion.Inforum.domain.memo.repository;


import Funssion.Inforum.domain.memo.entity.Memo;

import java.util.List;

public interface MemoRepository {

    Memo create(Memo memo);
    List<Memo> findAllByDaysOrderByLikes(Long days);
    List<Memo> findAllOrderById();
    List<Memo> findAllByUserIdOrderById(Long userId);
    Memo findById(Long id);
    Memo update(Memo memo, Long memoId, Long authorId);
    void delete(Long id);
}
