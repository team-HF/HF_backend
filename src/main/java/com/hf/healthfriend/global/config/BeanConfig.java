package com.hf.healthfriend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hf.healthfriend.global.util.HttpCookieUtils;
import com.hf.healthfriend.global.util.SecuredHttpCookieUtils;
import com.hf.healthfriend.global.util.file.FileUrlResolver;
import com.hf.healthfriend.global.util.file.LocalFileUrlResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(HttpCookieUtils.class)
    public HttpCookieUtils securedHttpCookieUtils(@Value("${client.domain}") String clientDomain) {
        return new SecuredHttpCookieUtils(clientDomain);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean(FileUrlResolver.class)
    public LocalFileUrlResolver localWindowsFileUrlResolver(@Value("${server.port}") String port) {
        return new LocalFileUrlResolver("http://localhost:" + port); // 로컬을 염두에 뒀으니까... 걍 하드코딩해도 될 거라 생각...
    }
}
