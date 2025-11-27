package com.gestiondestock.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.gestiondestock")
@EnableJpaRepositories(basePackages = "com.gestiondestock.repository")
@EntityScan(basePackages = "com.gestiondestock.entity")
public class BackendApplication {

	public static void main(String[] args) {
            
		SpringApplication.run(BackendApplication.class, args);
	}

}

