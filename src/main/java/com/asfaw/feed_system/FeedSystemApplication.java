package com.asfaw.feed_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaAuditing
@EnableAsync
@ConfigurationPropertiesScan
@SpringBootApplication
public class FeedSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedSystemApplication.class, args);
	}

}
