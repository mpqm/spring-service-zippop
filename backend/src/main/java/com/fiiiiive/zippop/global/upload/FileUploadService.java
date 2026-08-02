package com.fiiiiive.zippop.global.upload;

import com.fiiiiive.zippop.global.base.ServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {
    
    // 단일 파일 업로드
    String singleUpload(MultipartFile file) throws ServiceException;

    // 복수 파일 업로드
    List<String> multipleUpload(MultipartFile[] files) throws ServiceException;

}
