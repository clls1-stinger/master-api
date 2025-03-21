package com.life.master_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MasterApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MasterApiApplication.class, args);
		System.setProperty("logging.pattern.console", "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wex");

	}

}
