package io.java;

import io.java.connection.DataStaxAstraProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

@SpringBootApplication
//@RestController
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class SpringBookTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBookTrackerApplication.class, args);
	}

	/*
	@RequestMapping("/user")
	public String user(@AuthenticationPrincipal OAuth2User principal) {
		System.out.println(principal);

		return principal.getAttribute("User Attributes");
	}*/


	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties){
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return cqlSessionBuilder -> cqlSessionBuilder.withCloudSecureConnectBundle(bundle);
	}

}
