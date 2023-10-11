package Funssion.Inforum.domain.mypage.repository;


import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.member.dto.response.IsProfileSavedDto;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.domain.History;

import java.time.LocalDate;
import java.util.List;

public interface MyRepository {

    List<History> findMonthlyHistoryByUserId(Long userId, Integer year, Integer month);
    void updateHistory(Long userId, PostType postType, Sign sign, LocalDate curDate);
    void createHistory(Long userId, PostType postType);

    MemberProfileEntity findProfileByUserId(Long userID);

    IsProfileSavedDto createProfile(Long userId, MemberProfileEntity MemberProfileEntity);
    IsProfileSavedDto updateProfile(Long userId, MemberProfileEntity MemberProfileEntity);

    String findProfileImageNameById(Long userId);

}
