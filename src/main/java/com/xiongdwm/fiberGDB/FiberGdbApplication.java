package com.xiongdwm.fiberGDB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories;

@SpringBootApplication
@EnableReactiveNeo4jRepositories
@EntityScan
public class FiberGdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiberGdbApplication.class, args);
		System.out.println("========================web app started=========================>>");
	}

}
