package Funssion.Inforum.s3.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageDto {
    private final String imagePath;
    private final String imageName;
}
