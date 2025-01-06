package com.danahub.zipitda;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(exclude = MybatisAutoConfiguration.class)
@EnableJpaAuditing
@MapperScan(basePackages = "com.danahub.zipitda.**.mapper")
public class ZipitdaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZipitdaApplication.class, args);
	}

}