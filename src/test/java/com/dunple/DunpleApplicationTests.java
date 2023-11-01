package com.dunple;

import com.dunple.api.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppConfig.class)
@SpringBootApplication
class DunpleApplicationTests {

	@Test
	void contextLoads() {
	}

}
