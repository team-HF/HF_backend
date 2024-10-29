package com.hf.healthfriend.global.file.local;

import com.hf.healthfriend.global.file.FileUploader;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * 로컬 환경에서 사용되는 파일 업로더
 * ServletContext의 Real Path에 파일이 저장된다.
 *
 * @author PGD
 */
@Slf4j
public class LocalFileUploader implements FileUploader {
    private static final int BUFFER_SIZE = 1024;

    private final String rootPath;

    public LocalFileUploader(ServletContext servletContext) {
        String realContextRoot = servletContext.getRealPath("/");
        if (realContextRoot != null && realContextRoot.endsWith(File.separator)) {
            realContextRoot = realContextRoot.substring(0, realContextRoot.length() - File.separator.length());
        } else if (realContextRoot == null) {
            realContextRoot = "";
        }
        this.rootPath = realContextRoot;
    }

    @Override
    public void uploadFile(String filePath, MultipartFile multipartFile) throws IOException {
        uploadFile(filePath, multipartFile.getBytes());
    }

    @Override
    public void uploadFile(String filePath, byte[] bytes) throws IOException {
        String path = readyFileEnv(filePath);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path))) {
            bos.write(bytes);
        }
    }

    @Override
    public void uploadFile(String filePath, InputStream is) throws IOException {
        String path = readyFileEnv(filePath);
        byte[] buffer = new byte[BUFFER_SIZE];
        try (BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path))) {
            while (bis.read(buffer) != -1) {
                bos.write(buffer);
            }
        }
    }

    private String readyFileEnv(String filePath) throws IOException {
        String path = this.rootPath + filePath.replace("/", File.separator);
        log.info("path={}", path);
        File dirPath = new File(path.substring(0, path.lastIndexOf(File.separator)));
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
        return path;
    }
}
