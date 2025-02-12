package com.hf.healthfriend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SseConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/hf/connect/sse")
                .allowedOrigins("*")  // 클라이언트의 도메인
                .allowedMethods("GET")  // 허용할 HTTP 메소드
                .allowedHeaders("Last-Event-ID")  // 허용할 헤더
                .allowCredentials(false);  // 인증정보 전송 허용
    }
}
