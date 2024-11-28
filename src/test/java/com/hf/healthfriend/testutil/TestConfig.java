package com.hf.healthfriend.testutil;

import com.hf.healthfriend.global.file.FileUploader;
import com.hf.healthfriend.global.file.FileUrlResolver;
import com.hf.healthfriend.global.file.local.LocalFileUrlResolver;
import com.hf.healthfriend.global.util.mapping.BeanMapper;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@TestConfiguration
public class TestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @ConditionalOnMissingBean(BeanMapper.class)
    @Bean
    public BeanMapper beanMapper() {
        return new BeanMapper();
    }

    @Bean
    @ConditionalOnMissingBean(FileUrlResolver.class)
    public LocalFileUrlResolver localWindowsFileUrlResolver() {
        return new LocalFileUrlResolver("http://localhost:8080");
    }

    @Bean
    @ConditionalOnMissingBean(FileUploader.class)
    public FileUploader localMultipartFileUploader() {
        return new FileUploader() {

            @Override
            public void uploadFile(String filePath, MultipartFile multipartFile) throws IOException {
            }

            @Override
            public void uploadFile(String filePath, byte[] bytes) throws IOException {
            }

            @Override
            public void uploadFile(String filePath, InputStream is) throws IOException {
            }
        };
    }
}