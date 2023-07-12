package Funssion.Inforum.swagger.mypage.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
@Getter @Setter
@AllArgsConstructor
public class History {
    @Schema(description="히스토리를 날짜,개수로 해당 날짜에 올린 게시글 수를 보여줌", example = "{\"2023-07-10\": 5, \"2023-07-11\": 8, \"2023-07-12\": 3}")
    HashMap<String,Integer> history;
}
