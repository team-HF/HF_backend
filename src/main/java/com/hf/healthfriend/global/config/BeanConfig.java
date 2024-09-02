package com.hf.healthfriend.global.config;

import com.hf.healthfriend.global.util.HttpCookieUtils;
import com.hf.healthfriend.global.util.NoSecurityHttpCookieUtils;
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
    public HttpCookieUtils noSecurityHttpCookieUtils() {
        return new NoSecurityHttpCookieUtils();
    }
}
