package Funssion.Inforum.mypage.repository;


import Funssion.Inforum.memo.dto.MemoListDto;
import Funssion.Inforum.mypage.dto.MyRecordNumDto;

import java.util.List;

public interface MyRepository {

//    List<MyRecordNumDto> findRecordNumByUserId(String userId);
    List<MemoListDto> findAllByUserId(int userId);


}
