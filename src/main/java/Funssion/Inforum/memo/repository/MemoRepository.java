package Funssion.Inforum.memo.repository;


import Funssion.Inforum.memo.entity.Memo;
import Funssion.Inforum.memo.form.MemoSaveForm;

import java.util.List;

public interface MemoRepository {

    Memo create(int userId, String userName, MemoSaveForm form);
    List<Memo> findAllByUserId(int userId);
    public List<Memo> findAllByPeriod(int period);
    Memo findById(int id);
    public Memo update(int id, MemoSaveForm form);
    void delete(int id);
}
