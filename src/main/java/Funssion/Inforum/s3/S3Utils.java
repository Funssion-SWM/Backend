package Funssion.Inforum.s3;

import Funssion.Inforum.domain.member.dto.request.MemberInfoDto;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public abstract class S3Utils{

    public static ObjectMetadata getObjectMetaData(MultipartFile file){
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
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
}
