package com.hf.healthfriend.global.util.file.local;

import com.hf.healthfriend.global.util.file.MultipartFileUploader;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 로컬 환경에서 사용되는 파일 업로더
 * ServletContext의 Real Path에 파일이 저장된다.
 *
 * @author PGD
 */
@Slf4j
public class LocalMultipartFileUploader implements MultipartFileUploader {
    private final String rootPath;

    public LocalMultipartFileUploader(ServletContext servletContext) {
        String realContextRoot = servletContext.getRealPath("/");
        if (realContextRoot != null && realContextRoot.endsWith(File.separator)) {
            realContextRoot = realContextRoot.substring(0, realContextRoot.length() - File.separator.length());
        } else if (realContextRoot == null) {
            realContextRoot = "";
        }
        this.rootPath = realContextRoot;
    }

    public void uploadFile(String filePath, MultipartFile multipartFile) throws IOException {
        uploadFile(filePath, multipartFile.getBytes());
    }

    public void uploadFile(String filePath, byte[] bytes) throws IOException {
        String path = this.rootPath + filePath.replace("/", File.separator);
        log.info("path={}", path);
        File dirPath = new File(path.substring(0, path.lastIndexOf(File.separator)));
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path))) {
            bos.write(bytes);
        }
    }
}
