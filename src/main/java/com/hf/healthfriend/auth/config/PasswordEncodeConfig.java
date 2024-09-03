package com.hf.healthfriend.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncodeConfig {

    @Bean
    @Profile("prod") // TODO: Profile을 한번 잘 생각해 보기
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Default PasswordEncoder.
     * 로컬 개발 환경에서 테스트용으로 사용할 PasswordEncoder.
     * 비밀번호를 암호화하기 않는다.
     * @return 비밀번호를 암호화하지 않는 PasswordEncoder
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder noEncryptionPasswordEncoder() {
        return new PasswordEncoder() {

            @Override
            public String encode(CharSequence rawPassword) {
                return String.valueOf(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return String.valueOf(rawPassword).equals(encodedPassword);
            }
        };
    }
}
