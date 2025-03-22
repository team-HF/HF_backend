package com.hf.healthfriend.global.file.s3;

import com.hf.healthfriend.global.file.FileUploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
@Profile("prod")
public class AwsS3FileUploader implements FileUploader {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public void uploadFile(String filePath, MultipartFile multipartFile) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void uploadFile(String filePath, byte[] bytes) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void uploadFile(String filePath, InputStream is) throws IOException {
        throw new UnsupportedOperationException();
    }
}
