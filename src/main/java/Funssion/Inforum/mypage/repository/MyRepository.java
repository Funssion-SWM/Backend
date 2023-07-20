package Funssion.Inforum.mypage.repository;


import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.memo.dto.MemoListDto;
import Funssion.Inforum.mypage.dto.MyRecordNumDto;
import Funssion.Inforum.mypage.dto.MyUserInfoDto;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MyRepository {

    List<MemoListDto> findAllByUserId(int userId);
    List<MyRecordNumDto> findRecordNumByUserId(int userId);
    Optional<MyUserInfoDto> findUserInfoByUserId(int userId);
    void updateHistory(PostType type, int postId, int userId);

}
