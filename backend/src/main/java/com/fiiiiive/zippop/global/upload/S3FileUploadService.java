package com.fiiiiive.zippop.global.upload;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("s3FileUploadService")
@RequiredArgsConstructor
public class S3FileUploadService implements FileUploadService {
    private final ImageUploadValidator imageUploadValidator;

    @Value("${upload.s3.bucket}")
    private String bucketName;

    @Value("${upload.s3.region.static}")
    private String s3Region;

    private final AmazonS3 amazonS3;

    // 단일 파일 업로드
    public String singleUpload(MultipartFile file) {
        if(file != null && !file.isEmpty()) {
            ImageUploadValidator.ValidatedImage image = imageUploadValidator.validate(file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(image.mediaType());
            try {
                String saveFileName = UUID.randomUUID() + "." + image.extension();
                amazonS3.putObject(bucketName, saveFileName, file.getInputStream(), metadata);
                return "https://" + bucketName + ".s3." + s3Region +".amazonaws.com/" + saveFileName;
            } catch (IOException | AmazonS3Exception e) {
                throw new ServerException(ServerErrorCode.FILE_UPLOAD_ERROR, e);
            }
        }else {
            return null;
        }
    }

    // 복수 파일 업로드
    public List<String> multipleUpload(MultipartFile[] files) {
        if(files != null) {
            List<String> fileNames = new ArrayList<>();
            try {
                for (MultipartFile file : files) {
                    if (file == null || file.isEmpty()) continue;
                    ImageUploadValidator.ValidatedImage image = imageUploadValidator.validate(file);
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(file.getSize());
                    metadata.setContentType(image.mediaType());
                    String saveFileName = UUID.randomUUID() + "." + image.extension();
                    amazonS3.putObject(bucketName, saveFileName, file.getInputStream(), metadata);
                    fileNames.add("https://" + bucketName + ".s3." + s3Region +".amazonaws.com/" + saveFileName);
                }
                return fileNames.isEmpty() ? null : fileNames;
            } catch (RuntimeException | IOException e) {
                deleteAll(fileNames);
                if (e instanceof ServerException serverException) throw serverException;
                throw new ServerException(ServerErrorCode.FILE_UPLOAD_ERROR, e);
            }
        }else {
            return null;
        }
    }

    @Override
    public void delete(String fileUrl) {
        if (fileUrl == null) return;
        String prefix = "https://" + bucketName + ".s3." + s3Region + ".amazonaws.com/";
        if (!fileUrl.startsWith(prefix)) return;
        String key = fileUrl.substring(prefix.length());
        if (key.isBlank() || key.contains("/") || key.contains("\\")) return;
        try {
            amazonS3.deleteObject(bucketName, key);
        } catch (AmazonS3Exception ignored) {
            // DB 처리는 성공한 상태이므로 정리 실패가 응답을 뒤집지 않도록 한다.
        }
    }

}
