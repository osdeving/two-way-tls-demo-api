package com.willams.twowaytls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TwoWayTlsDemoApiApplication implements ApplicationRunner {

	@Autowired
	RequestClientService requestClientService;

	public static void main(String[] args) {
		SpringApplication.run(TwoWayTlsDemoApiApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		String msg = requestClientService.getRequest("1").block();
		System.out.println("Recebido do servidor: " +  msg);
	}
}
