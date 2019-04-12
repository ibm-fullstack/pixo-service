package com.pixo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.pixo.property.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties.class)
public class PixoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PixoApplication.class, args);
	}
}
