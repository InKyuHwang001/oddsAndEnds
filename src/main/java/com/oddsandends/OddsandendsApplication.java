package com.oddsandends;

import com.oddsandends.api.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class OddsandendsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OddsandendsApplication.class, args);
	}

}
