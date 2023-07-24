package Funssion.Inforum.domain.mypage.repository;


import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.memo.dto.MemoListDto;
import Funssion.Inforum.domain.mypage.dto.MyRecordNumDto;
import Funssion.Inforum.domain.mypage.dto.MyUserInfoDto;

import java.util.List;
import java.util.Optional;

public interface MyRepository {

    List<MemoListDto> findAllByUserId(int userId);
    List<MyRecordNumDto> findRecordNumByUserId(int userId);
    Optional<MyUserInfoDto> findUserInfoByUserId(int userId);
    void createHistory(int userId);
    void updateCreationToHistory(PostType type, int postId, int userId);
    void updateDeletionToHistory(PostType type, int postId, int userId);

}
