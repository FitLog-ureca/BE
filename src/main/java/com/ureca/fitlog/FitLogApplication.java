package com.ureca.fitlog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("com.ureca.fitlog.todos.mapper")
@MapperScan(basePackages = "com.ureca.fitlog")  // 10/22 16:30 추가 MyBatis Mapper 경로 전체를 스캔하도록 지정
public class FitLogApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitLogApplication.class, args);
	}

}
