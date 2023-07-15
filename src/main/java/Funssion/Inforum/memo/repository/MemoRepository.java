package Funssion.Inforum.memo.repository;


import Funssion.Inforum.memo.dto.MemoDto;
import Funssion.Inforum.memo.dto.MemoListDto;
import Funssion.Inforum.memo.dto.MemoSaveDto;

import java.util.List;

public interface MemoRepository {

    MemoDto create(int userId, String userName, MemoSaveDto form);
    List<MemoListDto> findAllByUserId(int userId);
    List<MemoListDto> findAllByPeriodWithMostPopular(int period);
    List<MemoListDto> findAllWithNewest();
    MemoDto findById(int id);
    MemoDto update(int id, MemoSaveDto form);
    void delete(int id);
}
