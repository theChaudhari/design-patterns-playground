package com.designpattern.config;

import com.designpattern.handler.ApiKeyAuthHandler;
import com.designpattern.handler.AuthHandler;
import com.designpattern.handler.JwtAuthHandler;
import com.designpattern.handler.OAuthHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthChainConfig {
    @Bean
    public AuthHandler authHandlerChain(ApiKeyAuthHandler apiKeyAuthHandler, JwtAuthHandler jwtAuthHandler, OAuthHandler oAuthHandler) {
        // Build the chain: ApiKey → JWT → OAuth → DefaultHandler (AbstractAuthHandler fallback)
        apiKeyAuthHandler.setNext(jwtAuthHandler).setNext(oAuthHandler);

        return apiKeyAuthHandler; // entry point of the chain
    }
}

