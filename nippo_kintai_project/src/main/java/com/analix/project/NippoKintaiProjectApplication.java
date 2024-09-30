package com.analix.project;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@RestController
public class NippoKintaiProjectApplication {
	
	private static final Logger log = LoggerFactory.getLogger(NippoKintaiProjectApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(NippoKintaiProjectApplication.class, args);
	}
	
//	@GetMapping("/hello")
//    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
//      return String.format("Hello %s!", name);
//    }
	@GetMapping("/hello")
    public String hello() {
        log.info("Hello World!");
        return "Hello World!";
    }
	
	// 暗号化API関係
	@PostConstruct
	public void init() {
		Security.addProvider(new BouncyCastleProvider());
	}


}
