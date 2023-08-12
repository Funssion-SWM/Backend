package Funssion.Inforum.domain.mypage.repository;


import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.mypage.domain.History;

import Funssion.Inforum.domain.member.dto.response.IsProfileSavedDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;

import java.sql.Date;
import java.util.List;

public interface MyRepository {

    List<History> findMonthlyHistoryByUserId(Long userId, Integer year, Integer month);
    MemberProfileEntity findProfileByUserId(Long userID);
    void updateHistory(Long userId, PostType postType, Sign sign, Date curDate);
    void createHistory(Long userId, PostType postType);

    IsProfileSavedDto updateProfile(Long userId, MemberProfileEntity MemberProfileEntity);

    String findProfileImageNameById(Long userId);

}
