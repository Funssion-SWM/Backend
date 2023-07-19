package Funssion.Inforum.mypage.repository;


import Funssion.Inforum.mypage.dto.MyRecordNumDto;

import java.util.List;

public interface MyRepository {

    List<MyRecordNumDto> findRecordNumByUserId(String userId);

}
