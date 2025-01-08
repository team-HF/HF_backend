package com.hf.healthfriend.global.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AWSConfig {
    @Value("${aws.region}")
    private String region;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void checkEnvironmentVariables() {
        System.out.println("AWS_ACCESS_KEY_ID: " + environment.getProperty("AWS_ACCESS_KEY_ID"));
        System.out.println("AWS_SECRET_ACCESS_KEY: " + environment.getProperty("AWS_SECRET_ACCESS_KEY"));
    }

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
