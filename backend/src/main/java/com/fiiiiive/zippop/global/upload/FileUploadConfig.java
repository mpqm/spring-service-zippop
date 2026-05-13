package com.fiiiiive.zippop.global.upload;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FileUploadConfig {
    
    @Value("${upload.type}")
    private String uploadType;

    @Value("${upload.s3.credentials.access-key}")
    private String accessKey;

    @Value("${upload.s3.credentials.secret-key}")
    private String secretKey;

    @Value("${upload.s3.region.static}")
    private String region;

    @Bean
    @Primary
    public FileUploadService fileUpload(
            @Qualifier("localFileUploadService") FileUploadService localFileUploadService,
            @Qualifier("s3FileUploadService") FileUploadService s3FileUploadService) {
        
        // application.yml에서 file.singleUpload.type 설정에 따라 서비스 선택
        // local: 로컬 파일 시스템 사용
        // s3: AWS S3 사용 (기본값은 local)
        if ("s3".equalsIgnoreCase(uploadType)) {
            return s3FileUploadService;
        } else {
            return localFileUploadService;
        }
    }

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials credentials =
                new BasicAWSCredentials(accessKey,secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

}
