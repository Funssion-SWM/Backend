package Funssion.Inforum.domain.post.memo.dto.request;

import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.response.MemoDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor // cannot deserialize from object value (no delegate- or property-based creator) 오류 해결 위한 빈 생성자 생성
public class MemoSaveDto {
    @NotBlank(message = "제목을 입력해주세요")
    private String memoTitle;
    private String memoDescription;
    @NotBlank(message = "내용을 입력해주세요")
    private String memoText;
    @NotBlank(message = "색을 지정해주세요")
    private String memoColor;

    private List<String> memoTags;
    @Builder.Default
    private Boolean isTemporary = false;

    public MemoSaveDto(String memoTitle, String memoDescription, String memoText, String memoColor,List<String> memoTags, Boolean isTemporary) {
        this.memoTitle = memoTitle;
        this.memoDescription = memoDescription;
        this.memoText = memoText;
        this.memoColor = memoColor;
        this.memoTags = memoTags;
        this.isTemporary = isTemporary;
    }

    public static MemoSaveDto valueOf(MemoDto memo) {
        return MemoSaveDto.builder()
                .memoTitle(memo.getMemoTitle())
                .memoColor(memo.getMemoColor())
                .memoTags(memo.getMemoTags())
                .memoText(memo.getMemoText())
                .memoDescription(memo.getMemoDescription())
                .isTemporary(memo.getIsTemporary())
                .build();
    }

    public static MemoSaveDto valueOf(Memo memo) {
        return MemoSaveDto.builder()
                .memoTitle(memo.getTitle())
                .memoColor(memo.getColor())
                .memoTags(memo.getMemoTags())
                .memoText(memo.getText())
                .memoDescription(memo.getDescription())
                .isTemporary(memo.getIsTemporary())
                .build();
    }
}
