package com.fiiiiive.zippop.global.upload;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
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

@Service("localFileUpload")
@RequiredArgsConstructor
@Slf4j
public class LocalFileUpload implements FileUpload {
    
    @Value("${file-upload.local.path}")
    private String uploadPath;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${server.address:localhost}")
    private String serverAddress;

    // 단일 파일 업로드
    public String upload(MultipartFile file) throws BaseException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 업로드 디렉토리 생성
            createUploadDirectory();
            
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
            throw new BaseException(BaseResponseMessage.FILE_UPLOAD_FAIL, e.getMessage());
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
            createUploadDirectory();
            
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
            throw new BaseException(BaseResponseMessage.FILE_UPLOAD_FAIL, e.getMessage());
        }
    }
    
    // 업로드 디렉토리 생성
    private void createUploadDirectory() throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
            log.info("업로드 디렉토리 생성: {}", uploadPath);
        }
    }
    
//    // 파일 삭제
//    public boolean deleteFile(String fileName) {
//        try {
//            Path filePath = Paths.get(uploadPath, fileName);
//            if (Files.exists(filePath)) {
//                Files.delete(filePath);
//                log.info("파일 삭제 완료: {}", fileName);
//                return true;
//            }
//            return false;
//        } catch (IOException e) {
//            log.error("파일 삭제 실패: {}", e.getMessage());
//            return false;
//        }
//    }
}
