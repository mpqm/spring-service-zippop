package com.fiiiiive.zippop.global.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
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

    @Value("${singleUpload.s3.bucket}")
    private String bucketName;

    @Value("${singleUpload.s3.region.static}")
    private String s3Region;

    private final AmazonS3 amazonS3;

    // 단일 파일 업로드
    public String singleUpload(MultipartFile file) throws BaseException {
        if(file != null) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            try {
                String saveFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                amazonS3.putObject(bucketName, saveFileName, file.getInputStream(), metadata);
                return "https://" + bucketName + ".s3." + s3Region +".amazonaws.com/" + saveFileName;
            } catch (IOException | AmazonS3Exception e) {
                throw new BaseException(BaseMessage.FILE_UPLOAD_FAIL, e.getMessage());
            }
        }else {
            return null;
        }
    }

    // 복수 파일 업로드
    public List<String> multipleUpload(MultipartFile[] files) throws BaseException {
        if(files != null) {
            List<String> fileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType());
                try {
                    String saveFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    amazonS3.putObject(bucketName, saveFileName, file.getInputStream(), metadata);
                    fileNames.add("https://" + bucketName + ".s3." + s3Region +".amazonaws.com/" + saveFileName);
                } catch (IOException | AmazonS3Exception e) {
                    throw new BaseException(BaseMessage.FILE_UPLOAD_FAIL, e.getMessage());
                }
            }
            return fileNames;
        }else {
            return null;
        }
    }

}
