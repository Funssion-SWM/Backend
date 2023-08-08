package Funssion.Inforum.domain.mypage.repository;


import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.domain.mypage.entity.History;

import java.util.List;

public interface MyRepository {

    List<History> findMonthlyHistoryByUserId(Long userId, Integer year, Integer month);
    void updateHistory(Long userId, PostType postType, Sign sign);
    void createHistory(Long userId, PostType postType);

}
