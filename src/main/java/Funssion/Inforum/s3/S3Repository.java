package Funssion.Inforum.s3;

import Funssion.Inforum.common.exception.ImageIOException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ListIterator;

@Slf4j
@Repository
@RequiredArgsConstructor
public class S3Repository {

    @Value("${aws.s3.cloudfront-url}")
    private String cloudFrontURL;

    @Value("${aws.s3.bucket-name}")
    private String defaultBucketName;
    private final AmazonS3 s3Client;

    public String createFolder(String bucketName, String folderName) {
        s3Client.putObject(bucketName, folderName + "/", new ByteArrayInputStream(new byte[0]), new ObjectMetadata());
        return S3Utils.generateNewBucketName(bucketName, folderName);
    }

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

    public void deleteAll(String folderName) {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(defaultBucketName)
                .withPrefix(folderName);
        ListObjectsV2Result listObjectsV2Result = s3Client.listObjectsV2(listObjectsV2Request);
        ListIterator<S3ObjectSummary> listIterator = listObjectsV2Result.getObjectSummaries().listIterator();

        while (listIterator.hasNext()){
            S3ObjectSummary objectSummary = listIterator.next();
            DeleteObjectRequest request = new DeleteObjectRequest(defaultBucketName,objectSummary.getKey());
            s3Client.deleteObject(request);
        }
    }

    private String getImageURL(String bucketName, String imageName) {
        return cloudFrontURL + s3Client.getUrl(bucketName, imageName).getPath().substring(15);
    }
}
