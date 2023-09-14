package Funssion.Inforum.s3;

import Funssion.Inforum.common.exception.ImageIOException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Repository
@RequiredArgsConstructor
public class S3Repository {

    @Value("${aws.s3.cloudfront-url}")
    private String cloudFrontURL;
    private final AmazonS3 s3Client;


    public String upload(MultipartFile memberProfileImage, String bucketName, String imageName) {
        try {
            ObjectMetadata imageMetaData = S3Utils.getObjectMetaData(memberProfileImage);
            s3Client.putObject(bucketName, imageName, memberProfileImage.getInputStream(), imageMetaData);
            return getImageURL(bucketName, imageName);
        } catch (IOException e) {
            throw new ImageIOException("프로필 이미지 IO Exception 발생", e);
        }
    }

    public void delete(String bucketName, String imageName){
        String imageNameInS3 = S3Utils.parseImageNameOfS3(imageName);
        s3Client.deleteObject(bucketName,imageNameInS3);
    }

    public String getImageURL(String bucketName, String imageName) {
        return cloudFrontURL + s3Client.getUrl(bucketName, imageName).getPath().substring(15);
    }
}
