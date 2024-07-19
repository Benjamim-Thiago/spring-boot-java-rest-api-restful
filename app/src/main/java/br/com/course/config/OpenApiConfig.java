package br.com.course.config;

import org.springframework.boot.SpringBootVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
class OpenApiConfig {
	
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("RESTful API with Java " +
						System.getProperty("java.version") +
						" and Spring Boot " + SpringBootVersion.getVersion()
				).version("v1")
				.description("Some description about your API")
				.termsOfService("https://docment-api/my-courses")
				.license(
					new License()
						.name("Apache 2.0")
						.url("https://docment-api/my-courses")
					)
				);
	}

}
