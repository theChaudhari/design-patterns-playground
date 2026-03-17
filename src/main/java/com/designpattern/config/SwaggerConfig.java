package com.designpattern.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Singleton Pattern API")
                        .description("Demonstrates all 5 Singleton implementations — Eager, Lazy, Synchronized, Double-Checked, Enum — using an Audit Logger use case")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("theChaudhari")
                                .url("https://github.com/theChaudhari/design-patterns-playground")));
    }

}