package com.bituan.payjor;

import com.bituan.payjor.config.RsaKeyProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@OpenAPIDefinition(
        servers ={
                @Server(url = "https://payjor.up.railway.app", description = "A friendly wallet"),
        }
                ,
        info = @Info(
                contact = @Contact(
                        name = "PayJor"
                ),
                description = "Wallet App",
                title = "PayJor Documentation",
                version = "1.0"
        ),
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class PayjorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayjorApplication.class, args);
	}

}
