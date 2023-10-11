package Funssion.Inforum.s3;

import Funssion.Inforum.domain.member.dto.request.MemberInfoDto;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public abstract class S3Utils{

    public static final int MAX_PROFILE_IMAGE_SIZE = 2_097_152;
    public static ObjectMetadata getObjectMetaData(MultipartFile file){
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }

    public static String generateNewBucketName(String bucketName, String folderName) {
        return bucketName + "/" + folderName;
    }

    public static String parseImageNameOfS3(String imagePathS3){
        int startIndexOfParsing = imagePathS3.lastIndexOf("/");
        return imagePathS3.substring(startIndexOfParsing+1);
    }

    public static String generateImageNameOfS3(MemberInfoDto memberInfoDto, Long userId) {
        if(memberInfoDto.getImage().isEmpty()) return "";
        String fileName = UUID.randomUUID()+ "-" + userId;
        return fileName;
    }

    public static String generateImageNameOfS3(Long userId) {
        String fileName = UUID.randomUUID()+ "-" + userId;
        return fileName;
    }
}
