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
        return new
                OpenAPI().info(new Info()
                         .title("Rate Limit Proxy Pattern API")
                         .description("Demonstrates Proxy Design Pattern — API Rate Limiting (5 req/min per user) with In-Memory Caching")
                         .version("1.0.0").contact(new Contact().name("theChaudhari").url("https://github.com/theChaudhari/design-patterns-playground")));
    }

}