package com.hf.healthfriend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HealthfriendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthfriendApplication.class, args);
	}

}
