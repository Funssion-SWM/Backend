package Funssion.Inforum.mypage.entity;

import lombok.Builder;
import lombok.Getter;
import org.json.JSONArray;

@Getter
@Builder
public class MyHistoryEntity {

    private int userId;
    private JSONArray history;
}
