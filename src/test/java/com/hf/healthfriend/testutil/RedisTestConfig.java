package com.hf.healthfriend.testutil;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;

@TestConfiguration
public class RedisTestConfig {
    @Bean
    public RedissonClient redissonClient() {
        // Redis TestContainer 설정
        GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.0.8-alpine")
                .withExposedPorts(6379);
        redisContainer.start();

        Config config = new Config();
        String redisAddress = "redis://" + redisContainer.getHost() + ":" + redisContainer.getMappedPort(6379);
        config.useSingleServer().setAddress(redisAddress);

        return Redisson.create(config);
    }
}
