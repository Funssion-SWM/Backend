package Funssion.Inforum.swagger.mypage.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter @Setter
@AllArgsConstructor
public class ContentList {
    @Schema(description="메모리스트를 배열로 조회")
    List<Memo> stories;
}
