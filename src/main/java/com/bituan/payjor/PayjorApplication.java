package com.bituan.payjor;

import com.bituan.payjor.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class PayjorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayjorApplication.class, args);
	}

}
