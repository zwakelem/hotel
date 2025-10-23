package za.co.simplitate.hotelbooking.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import za.co.simplitate.hotelbooking.services.S3Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.bucket-url}")
    private String bucketUrl;

    @Override
    public String uploadFile(MultipartFile file) {
        log.info("uploadFile: uploading file to S3 bucket");
        
        if (!file.getContentType().startsWith("image/")) {
            log.error("uploadFile: not an image");
            throw new IllegalArgumentException("Only image file allowed!!");
        }

        try {
            S3Client s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .build();

            // Generate unique filename for image
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String key = "room-images/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            s3Client.close();

            String imageUrl = bucketUrl + key;
            log.info("uploadFile: file uploaded successfully to {}", imageUrl);
            return imageUrl;

        } catch (IOException e) {
            log.error("uploadFile: error while uploading file to S3", e);
            throw new IllegalArgumentException("Error uploading file to S3: " + e.getMessage());
        }
    }
}
