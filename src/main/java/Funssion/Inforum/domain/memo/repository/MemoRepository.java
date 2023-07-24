package Funssion.Inforum.domain.memo.repository;


import Funssion.Inforum.domain.memo.dto.MemoDto;
import Funssion.Inforum.domain.memo.dto.MemoListDto;
import Funssion.Inforum.domain.memo.dto.MemoSaveDto;

import java.util.List;
import java.util.Optional;

public interface MemoRepository {

    Integer create(Integer userId, String userName, MemoSaveDto form);
    List<MemoListDto> findAllByPeriodWithMostPopular(Integer period);
    List<MemoListDto> findAllWithNewest();
    Optional<MemoDto> findById(Integer id);
    String findByUserId(Integer userId);
    Integer update(Integer memoId, Integer userId, MemoSaveDto form);
    Integer delete(Integer id);
}
