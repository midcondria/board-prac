package com.dunple.api;

import com.dunple.api.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppConfig.class)
@SpringBootApplication
public class DunpleApplication {

	public static void main(String[] args) {
		SpringApplication.run(DunpleApplication.class, args);
	}
}
