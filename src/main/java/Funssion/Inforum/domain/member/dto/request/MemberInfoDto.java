package Funssion.Inforum.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberInfoDto {
    private String nickname;
    private boolean isEmptyProfileImage;
    private MultipartFile image;
    private String introduce;
    private List<String> memberTags;

    public static MemberInfoDto createMemberInfo(boolean isEmptyProfileImage,MultipartFile image, String introduce, List<String> tags){
        return MemberInfoDto.builder()
                .image(image)
                .introduce(introduce)
                .isEmptyProfileImage(isEmptyProfileImage)
                .memberTags(tags)
                .build();
    }
}
