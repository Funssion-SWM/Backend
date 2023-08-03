package Funssion.Inforum.domain.memo.repository;


import Funssion.Inforum.domain.memo.entity.Memo;

import java.util.List;

public interface MemoRepository {

    Memo create(Memo memo);
    List<Memo> findAllByDaysOrderByLikes(Integer days);
    List<Memo> findAllOrderById();
    List<Memo> findAllByUserIdOrderById(Integer userId);
    Memo findById(Integer id);
    Memo update(Memo memo, Integer memoId);
    void delete(Integer id);
}
