package com.fiiiiive.zippop.global.config;

import com.fiiiiive.zippop.global.upload.FileUpload;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FileUploadConfig {
    
    @Value("${file-upload.type}")
    private String uploadType;
    
    @Bean
    @Primary
    public FileUpload fileUpload(
            @Qualifier("localFileUpload") FileUpload localFileUpload,
            @Qualifier("s3FileUpload") FileUpload s3FileUpload) {
        
        // application.yml에서 file.upload.type 설정에 따라 서비스 선택
        // local: 로컬 파일 시스템 사용
        // s3: AWS S3 사용 (기본값은 local)
        if ("s3".equalsIgnoreCase(uploadType)) {
            return s3FileUpload;
        } else {
            return localFileUpload;
        }
    }
}
