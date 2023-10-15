package Funssion.Inforum.domain.series.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
public class SeriesRequestDto {
    @NotEmpty(message = "시리즈 제목을 입력해주세요.")
    private String title;
    @NotEmpty(message = "시리즈 설명을 입력해주세요.")
    private String description;
    @Size(min = 2, message = "시리즈에 들어가는 메모는 2개 이상이어야 합니다.")
    private List<Long> memoIdList;
}
