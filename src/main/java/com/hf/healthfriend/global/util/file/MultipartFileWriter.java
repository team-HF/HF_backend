package com.hf.healthfriend.global.util.file;

import jakarta.servlet.ServletContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * BufferedOutputStream을 사용해 파일을 저장하는 boilerplate 코드를 Service 객체에서
 * 사용하고 싶지 않아서 정의하는 객체
 *
 * @author PGD
 */
@Component
public class MultipartFileWriter {
    private static final int BUFFER_SIZE = 8196;

    private final String rootPath;

    public MultipartFileWriter(ServletContext servletContext) {
        String realContextRoot = servletContext.getRealPath("/");
        if (realContextRoot.endsWith(File.separator)) {
            realContextRoot = realContextRoot.substring(0, realContextRoot.length() - File.separator.length());
        }
        this.rootPath = realContextRoot;
    }

    public void writeFile(String filePath, MultipartFile multipartFile) throws IOException {
        writeFile(filePath, multipartFile.getBytes());
    }

    public void writeFile(String filePath, byte[] bytes) throws IOException {

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(this.rootPath + filePath))) {
            bos.write(bytes);
        }
    }
}
