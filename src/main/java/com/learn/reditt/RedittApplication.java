package com.learn.reditt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RedittApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedittApplication.class, args);
	}

}
