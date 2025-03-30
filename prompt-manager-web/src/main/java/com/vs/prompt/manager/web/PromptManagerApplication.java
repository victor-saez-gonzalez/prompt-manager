package com.vs.prompt.manager.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.vs.prompt.manager.web",
		"com.vs.prompt.manager.service",
		"com.vs.prompt.manager.persistence",
		"com.vs.prompt.manager.common"
})
@EnableJpaRepositories(basePackages = "com.vs.prompt.manager.persistence.repository")
@EntityScan(basePackages = "com.vs.prompt.manager.model")
public class PromptManagerApplication {


	public static void main(String[] args) {
		SpringApplication.run(PromptManagerApplication.class, args);
	}

}
