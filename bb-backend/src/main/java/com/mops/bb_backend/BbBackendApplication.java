package com.mops.bb_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BbBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BbBackendApplication.class, args);
	}

}
