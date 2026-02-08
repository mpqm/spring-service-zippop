package com.fiiiiive.zippop.global.file;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("localFileUploadService")
@RequiredArgsConstructor
@Slf4j
public class LocalFileUploadService implements FileUploadService {
    
    @Value("${singleUpload.local.path}")
    private String uploadPath;
    
    @Value("${server.port}")
    private String serverPort;
    
    @Value("${server.address}")
    private String serverAddress;

    // 단일 파일 업로드
    public String singleUpload(MultipartFile file) throws BaseException {
        if (file == null || file.isEmpty()) return null;

        try {
            // 업로드 디렉토리 생성
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

            // 파일명 생성 (UUID + 원본파일명)
            String originalFilename = file.getOriginalFilename();
            String saveFileName = UUID.randomUUID() + "_" + originalFilename;
            
            // 파일 저장 경로
            Path filePath = Paths.get(uploadPath, saveFileName);
            
            // 파일 저장
            Files.copy(file.getInputStream(), filePath);
            
            // 접근 가능한 URL 반환
            String fileUrl = "http://" + serverAddress + ":" + serverPort + "/uploads/" + saveFileName;
            
            log.info("파일 업로드 완료: {}", fileUrl);
            return fileUrl;
            
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new BaseException(BaseMessage.FILE_UPLOAD_FAIL, e.getMessage());
        }
    }

    // 복수 파일 업로드
    public List<String> multipleUpload(MultipartFile[] files) throws BaseException {
        if (files == null || files.length == 0) {
            return null;
        }

        List<String> fileUrls = new ArrayList<>();
        
        try {
            // 업로드 디렉토리 생성
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("업로드 디렉토리 생성: {}", uploadPath);
            }
            
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    // 파일명 생성 (UUID + 원본파일명)
                    String originalFilename = file.getOriginalFilename();
                    String saveFileName = UUID.randomUUID() + "_" + originalFilename;
                    
                    // 파일 저장 경로
                    Path filePath = Paths.get(uploadPath, saveFileName);
                    
                    // 파일 저장
                    Files.copy(file.getInputStream(), filePath);
                    
                    // 접근 가능한 URL 생성
                    String fileUrl = "http://" + serverAddress + ":" + serverPort + "/uploads/" + saveFileName;
                    fileUrls.add(fileUrl);
                    
                    log.info("파일 업로드 완료: {}", fileUrl);
                }
            }
            
            return fileUrls;
            
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new BaseException(BaseMessage.FILE_UPLOAD_FAIL, e.getMessage());
        }
    }
    
    // 업로드 디렉토리 생성
    private void createUploadDirectory() throws IOException {

    }

}
