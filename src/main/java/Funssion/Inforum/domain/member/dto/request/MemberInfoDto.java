package Funssion.Inforum.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberInfoDto {
    private String nickname;
    private MultipartFile image;
    private String introduce;
    private String tags;

    public static MemberInfoDto createMemberInfo(MultipartFile image, String introduce, String tags){
        return MemberInfoDto.builder()
                .image(image)
                .introduce(introduce)
                .tags(tags)
                .build();
    }
}
