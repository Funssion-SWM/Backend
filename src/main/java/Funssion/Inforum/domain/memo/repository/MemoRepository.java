package Funssion.Inforum.domain.memo.repository;


import Funssion.Inforum.domain.memo.dto.MemoDto;
import Funssion.Inforum.domain.memo.dto.MemoListDto;
import Funssion.Inforum.domain.memo.dto.MemoSaveDto;

import java.util.List;

public interface MemoRepository {

    MemoDto create(int userId, String userName, MemoSaveDto form);
    List<MemoListDto> findAllByPeriodWithMostPopular(int period);
    List<MemoListDto> findAllWithNewest();
    MemoDto findById(int id);
    String findByUserId(Integer userId);
    MemoDto update(int id, MemoSaveDto form);
    void delete(int id);
}
