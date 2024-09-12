package com.hf.healthfriend.global.util.file;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 여러 구현체를 선택할 수 있는 Multipart 파일 업로더.
 * 개발 환경에 따라 로컬 환경에 파일을 저장할 수 있고, 네이버 클라우드 Object Storage,
 * S3 등등 여러 스토리지에 파일을 업로드할 수 있다.
 *
 * @author PGD
 */
@Component
public interface MultipartFileUploader {

    void uploadFile(String filePath, MultipartFile multipartFile) throws IOException;

    void uploadFile(String filePath, byte[] bytes) throws IOException;
}
