package com.moneylion.interview.spring.webserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@EnableAutoConfiguration
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
